import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink extends FairLossLink {

    ConcurrentHashMap<Integer[], String> ReceivedMessages = new ConcurrentHashMap<Integer[], String>();
    ConcurrentHashMap<Integer[], Boolean> MessagesAck = new ConcurrentHashMap<Integer[], Boolean>();


    public PerfectLink(int port) throws SocketException {
        super(port);
    }


    public void sendMessage(UdpMessage msg) throws IOException, NoSuchAlgorithmException {
        while(!MessagesAck.get(msg.getMessageUniqueIds())){
            try{
                super.sendMessage(msg);
                Thread.sleep(100);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startSendMessageThread() {
        Thread t = new Thread(() -> {
            Scanner myObj = new Scanner(System.in);

            System.out.println("Enter msg: ");

            // String input
            String message = myObj.nextLine();

            UdpMessage messageToSend = new UdpMessage(1, 1, message, MessageType.Message);

            MessagesAck.put(messageToSend.getMessageUniqueIds(), false);

            try {
                sendMessage(messageToSend);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        t.start();
    }

    public UdpMessage receiveMessage() throws IOException, ClassNotFoundException {
        return super.receiveMessage();
    }

    public void StartReceiveMessagesThread() {

        new Thread(() -> {
            while (true) {
                try {
                    UdpMessage messageReceived = receiveMessage();
                    PrettyPrintMessage(messageReceived);

                    processMessageReceived(messageReceived);

                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void processMessageReceived(UdpMessage msg) throws IOException, NoSuchAlgorithmException, InterruptedException {
        switch (msg.type()) {

            case Message -> {
                if (!isMessageDuplicate(msg)) {
                    ReceivedMessages.put(msg.getMessageUniqueIds(), msg.message());
                    /// send echo response to sender
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    UdpMessage ackMessage = new UdpMessage(1, 1, "message ack", MessageType.Ack);
                    super.sendMessage(ackMessage);
                }
            }

            case Ack -> MessagesAck.put(msg.getMessageUniqueIds(), true);

        }
    }

    //region Auxiliary methods

    private void PrettyPrintMessage(UdpMessage msg){
        System.out.println("message received");
        System.out.println("sendID: " + msg.senderID() +
                " || msgID: " + msg.messageID() +
                " ||  msg: " + msg.message() +
                " || type: " + msg.type()
        );
    }

    private boolean isMessageDuplicate(UdpMessage msg) {
        return ReceivedMessages.get(msg.getMessageUniqueIds()) != null;
    }

    //endregion


}
