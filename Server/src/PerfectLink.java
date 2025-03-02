import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink extends FairLossLink {

    ConcurrentHashMap<Integer, String> ReceivedMessages = new ConcurrentHashMap<Integer, String>();
    ConcurrentHashMap<Integer, Boolean> MessagesAck = new ConcurrentHashMap<Integer, Boolean>();


    public PerfectLink(int port) throws SocketException {
        super(port);
    }

    @Override
    public void sendMessage(UdpMessage msg) throws IOException, NoSuchAlgorithmException {
        while(!MessagesAck.get(msg.messageID())){
            try{
                super.sendMessage(msg);
                Thread.sleep(100);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public UdpMessage receiveMessage() throws IOException, ClassNotFoundException {
        return super.receiveMessage();
    }


    public void startSendMessageThread() {
        Thread t = new Thread(() -> {
            Scanner myObj = new Scanner(System.in);

            System.out.println("Enter msg: ");

            // String input
            String message = myObj.nextLine();

            UdpMessage messageToSend = new UdpMessage(1, 1, message, MessageType.Message);

            MessagesAck.put(messageToSend.messageID(), false);

            try {
                sendMessage(messageToSend);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        t.start();
    }

    public void StartReceiveMessagesThread() {

        new Thread(() -> {
            //UdpMessage msg = new UdpMessage();
            while (true) {

                try {
                    UdpMessage messageReceived = receiveMessage();
                    System.out.println("message received");
                    System.out.println("sendID: " + messageReceived.senderID() +
                            " || msgID: " + messageReceived.messageID() +
                            " ||  msg: " + messageReceived.message() +
                            " || type: " + messageReceived.type()
                    );

                    processMessageReceived(messageReceived);


                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();
    }

    boolean isMessageDuplicate(UdpMessage msg) {
        return ReceivedMessages.get(msg.messageID()) != null;
    }

    public void processMessageReceived(UdpMessage msg) throws IOException, NoSuchAlgorithmException, InterruptedException {
        switch (msg.type()) {

            case Message -> {
                if (!isMessageDuplicate(msg)) {
                    ReceivedMessages.put(msg.senderID(), msg.message());
                    /// send echo response to sender
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    UdpMessage messageToSend = new UdpMessage(1, 1, "message ack", MessageType.Ack);
                    sendMessage(messageToSend);
                }
            }

            case Ack -> MessagesAck.put(msg.messageID(), true);

        }


    }

}
