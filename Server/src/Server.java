import Communication.Collection.CollectMessage;
import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Links.LinkMessages.Base.LinkMessageType;
import Communication.Links.LinkMessages.UdpLinkMessage;
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

            // String input
            int epoch = Integer.parseInt(myObj.nextLine());
            String message = myObj.nextLine();

            CollectMessage msg =  new CollectMessage(epoch, message);
            UdpLinkMessage<CollectMessage> messageToSend = new UdpLinkMessage<>(1, 1, msg, LinkMessageType.Message);

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
                    MessageDeliveryTuple<ILinkMessage<CollectMessage>, Integer> messageReceived = authenticatedPerfectLink.receiveMessage();
                    if  (messageReceived != null){
                        authenticatedPerfectLink.processMessageReceived(messageReceived);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
