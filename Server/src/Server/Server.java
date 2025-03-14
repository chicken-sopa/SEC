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

    Blockchain blockchain = new Blockchain();


    public Server(int port, int processId, boolean isLeader) throws Exception {

        int quorumSize = 2;

        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth, getProcessId());
        conditionalCollect = new ConditionalCollect<>(authenticatedPerfectLink, quorumSize);

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

    /*private void startSendMessageProcedure() {
//        try {
//            conditionalCollect.startCollection();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        try {
            conditionalCollect.receiveMessages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }*/

    private void startConsensusLeaderThread() {
        new Thread(() -> {

            while (true) {
                try {
                    consensusBFT.leaderConsensusThread();
                    wait();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }


    void processMessageFromClient(AppendMessage message) throws Exception {
        SignedValTSPair newPair = new SignedValTSPair(0, message.getMessage(), message.getSenderId(), message.getSignature());

        consensusBFT.messagesFromClient.addLast(newPair);
        if (isLeader) {
            notifyAll(); // wake up if leader thread is sleeping
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
}


