import Communication.Helpers.Auxiliary;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.MessageType;
import Communication.Messages.UdpMessage;
import Communication.Security.DigitalSignatureAuth;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Server {

    AuthenticatedPerfectLink<UdpMessage>  authenticatedPerfectLink;
    DigitalSignatureAuth<UdpMessage>  digitalSignatureAuth;

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

            // String input
            String message = myObj.nextLine();

            UdpMessage messageToSend = new UdpMessage(1, 1, message, MessageType.Message);

            try {
                authenticatedPerfectLink.sendMessage(messageToSend, portToSend);
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
                    MessageDeliveryTuple<UdpMessage,Integer> messageReceived = authenticatedPerfectLink.receiveMessage();
                    if  (messageReceived != null){
                        Auxiliary.PrettyPrintUdpMessage(messageReceived.getMessage());
                        authenticatedPerfectLink.processMessageReceived(messageReceived);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
