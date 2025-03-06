import Communication.Collection.CollectMessage;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Security.DigitalSignatureAuth;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Server {

    AuthenticatedPerfectLink<CollectMessage>  authenticatedPerfectLink;
    DigitalSignatureAuth<CollectMessage>  digitalSignatureAuth;
    Boolean isLeader = false;

    public Server(int port) throws SocketException, NoSuchAlgorithmException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth);
    }

    public void init(){
        startSendMessageThread();
        startReceiveMessageThread();
    }

    private void startSendMessageThread() {
        Thread t = new Thread(() -> {
            Scanner myObj = new Scanner(System.in);
            Integer portToSend = 4551;

            // Prompt user for leader status
            System.out.println("Do you want to be the leader? (Type 'yes' to confirm)");
            String confirm = myObj.nextLine().trim().toLowerCase();

            isLeader = confirm.equals("yes");
            // If not the leader, exit the thread
            if (isLeader) {
                System.out.println("This process is the current leader.");
            }else{
                return;
            }

            // Prompt for epoch and message
            System.out.println("Enter epoch: ");
            int epoch = Integer.parseInt(myObj.nextLine());

            System.out.println("Enter message: ");
            String message = myObj.nextLine();

            CollectMessage msg = new CollectMessage(epoch, message);

            try {
                authenticatedPerfectLink.sendMessage(msg, portToSend);
                System.out.println("Message sent successfully.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t.start();
    }


    private void startReceiveMessageThread(){
        new Thread(() -> {
            while (true) {
                try {
                    CollectMessage messageReceived = authenticatedPerfectLink.receiveMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
