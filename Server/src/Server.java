import Communication.Collection.CollectMessage;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Security.DigitalSignatureAuth;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Server {

    AuthenticatedPerfectLink<CollectMessage>  authenticatedPerfectLink;
    DigitalSignatureAuth<CollectMessage>  digitalSignatureAuth;

    public Server(int port) throws SocketException, NoSuchAlgorithmException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(port, digitalSignatureAuth);
    }

    public void init(){
        startSendMessageThread();
        startReceiveMessageThread();
    }

    private void startSendMessageThread(){
        Thread t = new Thread(() -> {
            Scanner myObj = new Scanner(System.in);
            Integer portToSend = 4555;
            System.out.println("Enter msg: ");
            // input & msg creation
            int epoch = Integer.parseInt(myObj.nextLine());
            String message = myObj.nextLine();
            CollectMessage msg =  new CollectMessage(epoch, message);

            try {
                authenticatedPerfectLink.sendMessage(msg, portToSend);
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
