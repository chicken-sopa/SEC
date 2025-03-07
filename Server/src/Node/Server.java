package Node;

import Communication.Collection.BaseMessage;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Security.DigitalSignatureAuth;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Server {

    private static Server instance; // Static instance for global access
    AuthenticatedPerfectLink<BaseMessage>  authenticatedPerfectLink;
    DigitalSignatureAuth<BaseMessage>  digitalSignatureAuth;
    Boolean isLeader = false;
    Scanner sc;
    int processId;

    public Server(int port, int processId) throws SocketException, NoSuchAlgorithmException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth);
        sc = new Scanner(System.in);
        instance = this; // Store the singleton instance
    }

    public void init(){
        startSendMessageProcedure();
        startReceiveMessageThread();
        assertLeaderStatus();
    }

    private void assertLeaderStatus(){
        // Prompt user for leader status
        System.out.println("Do you want to be the leader? (Type 'yes' to confirm)");
        String confirm = sc.nextLine().trim().toLowerCase();

        isLeader = confirm.equals("yes");
        // If not the leader, exit the thread
        if (isLeader) {
            System.out.println("This process is the current leader.");
        }else{
            return;
        }
    }

    private void startSendMessageProcedure() {
        Integer portToSend = 4551;

        // Prompt for epoch and message
        System.out.println("Enter epoch: ");
        int epoch = Integer.parseInt(sc.nextLine());

        System.out.println("Enter message: ");
        String message = sc.nextLine();

        BaseMessage msg = new BaseMessage(epoch, message);

        try {
            authenticatedPerfectLink.sendMessage(msg, portToSend);
            System.out.println("Message sent successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startReceiveMessageThread(){
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage messageReceived = authenticatedPerfectLink.receiveMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public static int getProcessId() {
        if (instance == null) {
            throw new IllegalStateException("Node.Server instance has not been initialized.");
        }
        return instance.processId;
    }
}


