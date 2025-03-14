package Server;

import Communication.Collection.*;
import Communication.Consensus.Blockchain;
import Communication.Consensus.ConsensusBFT;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Messages.AppendMessage;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.MessageType;
import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;
import com.sec.Keys.KeyManager;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static Configuration.ProcessConfig.getProcessId;

public class Server {

    AuthenticatedPerfectLink<BaseMessage> authenticatedPerfectLink;
    DigitalSignatureAuth<BaseMessage> digitalSignatureAuth;
    ConditionalCollect<BaseMessage> conditionalCollect;
    ConsensusBFT consensusBFT;
    Boolean isLeader = false;
    Scanner sc;
    int processId;
    SignedWriteset writeset;


    private final Lock leaderThreadLock = new ReentrantLock();
    private final Condition condition = leaderThreadLock.newCondition();


    Blockchain blockchain;


    public Server(int port, int processId, boolean isLeader) throws Exception {

        int quorumSize = 2;

        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth, getProcessId());
        conditionalCollect = new ConditionalCollect<>(authenticatedPerfectLink, quorumSize);

        blockchain = new Blockchain(authenticatedPerfectLink);

        sc = new Scanner(System.in);
        this.isLeader = isLeader;
        this.processId = processId;
        try {
            writeset = new SignedWriteset(getProcessId(), KeyManager.getPrivateKey(processId));
            consensusBFT = new ConsensusBFT(quorumSize, authenticatedPerfectLink, processId, blockchain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        if (isLeader) {
            startConsensusLeaderThread();
        }
        startReceiveMessageThread();
    }


    private synchronized void startConsensusLeaderThread() {
        Thread t = new Thread(() -> {

            while (true) {
                try {
                    consensusBFT.leaderConsensusThread();
                    leaderThreadLock.lock();
                    try {
                        condition.await(); // Equivalent to wait()
                    } finally {
                        leaderThreadLock.unlock();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }); //.start();
        t.start();

    }


    void processMessageFromClient(AppendMessage message) throws Exception {
        SignedValTSPair newPair = new SignedValTSPair(0, message.getMessage(), message.getSenderId(), message.getSignature());

        consensusBFT.messagesFromClient.addLast(newPair);
        if (isLeader) {
            wakeUpConsensusLeader(); // wake up if leader thread is sleeping
        }

    }


    private void startReceiveMessageThread() {
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage messageReceived = authenticatedPerfectLink.receiveMessage();

                    if (messageReceived != null) {
                        if (messageReceived.getMessageType().equals(MessageType.APPEND)) {
                            processMessageFromClient((AppendMessage) messageReceived);
                        } else {
                            consensusBFT.processConsensusRequestMessage(messageReceived);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    private void wakeUpConsensusLeader() {
        leaderThreadLock.lock();
        try {
            condition.signal(); // Wake up one waiting thread
        } finally {
            leaderThreadLock.unlock();
        }
    }
}


