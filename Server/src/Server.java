import Communication.Collection.*;
import Communication.Consensus.Blockchain;
import Communication.Consensus.ConsensusBFT;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Security.DigitalSignatureAuth;
import Communication.Messages.AppendMessage;
import Communication.Messages.BaseMessage;
import Communication.Messages.StateMessage;
import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.Writeset.SignedWriteset;
import Keys.KeyManager;

import java.util.Scanner;

import static Configuration.ProcessConfig.getProcessId;

public class Server {

    AuthenticatedPerfectLink<BaseMessage>  authenticatedPerfectLink;
    DigitalSignatureAuth<BaseMessage>  digitalSignatureAuth;
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
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth);
        conditionalCollect = new ConditionalCollect<>(authenticatedPerfectLink,quorumSize);

        sc = new Scanner(System.in);
        this.isLeader = isLeader;
        this.processId = processId;
        try {
            writeset = new SignedWriteset(getProcessId(), KeyManager.getPrivateKey());
            consensusBFT = new ConsensusBFT(quorumSize, authenticatedPerfectLink, processId, blockchain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void init(){
        if(isLeader)
            startSendMessageProcedure();
        startReceiveMessageThread();
    }

    private void startSendMessageProcedure() {
        try {
            conditionalCollect.startCollection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            conditionalCollect.receiveMessages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void startReceiveMessageThread(){
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage messageReceived = authenticatedPerfectLink.receiveMessage();
                    if(messageReceived != null){
                        switch (messageReceived.getMessageType()) {
                            case INIT_COLLECT -> {
                                SignedValTSPair valTSPair = new SignedValTSPair(1, "aaaaaa", getProcessId(), KeyManager.getPrivateKey());
                                StateMessage response = new StateMessage(getProcessId(), valTSPair, writeset);
                                authenticatedPerfectLink.sendMessage(response, 4550 + messageReceived.getSenderId());
                            }
                            case APPEND -> {
                                AppendMessage message = (AppendMessage)messageReceived;
                                System.out.println("Received "+message.prettyPrint()+ " || Message -> "+message.getMessage());
                            }

                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}


