import Communication.Collection.*;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Security.DigitalSignatureAuth;
import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.ValTSPair.ValTSPair;
import Communication.Types.Writeset.SignedWriteset;
import Keys.KeyManager;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static Configuration.ProcessConfig.getProcessId;

public class Server {

    AuthenticatedPerfectLink<BaseMessage>  authenticatedPerfectLink;
    DigitalSignatureAuth<BaseMessage>  digitalSignatureAuth;
    ConditionalCollect<BaseMessage> conditionalCollect;
    Boolean isLeader = false;
    Scanner sc;
    int processId;
    SignedWriteset writeset;

    public Server(int port, int processId) throws SocketException, NoSuchAlgorithmException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth);
        conditionalCollect = new ConditionalCollect<>(authenticatedPerfectLink,2);
        sc = new Scanner(System.in);
        this.processId = processId;
        try {
            writeset = new SignedWriteset(getProcessId(), KeyManager.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void init(){
        assertLeaderStatus();
        if(isLeader)
            startSendMessageProcedure();
        startReceiveMessageThread();
    }

    private void assertLeaderStatus(){
        // Prompt user for leader status
        System.out.println("Do you want to be the leader? (Type 'yes' to confirm)");
        String confirm = sc.nextLine().trim().toLowerCase();

        isLeader = confirm.equals("yes");
        // If not the leader, exit the thread
        if (isLeader) {
            System.out.println("This process is the current leader.");
        }
    }

    private void startSendMessageProcedure() {

        // Prompt for epoch and message
//        System.out.println("Enter Destination Port: ");
//        int portToSend = Integer.parseInt(sc.nextLine());
//
        System.out.println("Enter epoch: ");
        int epoch = Integer.parseInt(sc.nextLine());
//
//        InitCollectMessage msg = new InitCollectMessage(epoch);
//
//        try {
//            authenticatedPerfectLink.sendMessage(msg, portToSend);
//            System.out.println("Message sent successfully.");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        try {
            conditionalCollect.startCollection(epoch);
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
                    switch (messageReceived.getMessageType()){
                        case MessageType.INIT_COLLECT -> {
                            System.out.println("Mensagem de inti collect recebida, de sender id" + messageReceived.getSenderId() + " a responder para o port" + (4550 + messageReceived.getSenderId()));
                            SignedValTSPair valTSPair = new SignedValTSPair(1,"aaaaaa",getProcessId(), KeyManager.getPrivateKey());
                            StateMessage response = new StateMessage(getProcessId(),valTSPair,writeset);
                            authenticatedPerfectLink.sendMessage(response,4550 + messageReceived.getSenderId());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}


