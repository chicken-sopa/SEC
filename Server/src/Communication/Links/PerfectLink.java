package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.MessageType;
import Communication.Messages.UdpMessage;

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


    public void sendMessage(UdpMessage msg, Integer portToSend) throws IOException, NoSuchAlgorithmException {
        while(!MessagesAck.get(msg.getMessageUniqueId())){
            try{
                super.sendMessage(msg, portToSend);
                Thread.sleep(100);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startSendMessageThread() {
        Thread t = new Thread(() -> {
            Scanner myObj = new Scanner(System.in);

            Integer portToSend = 4555;

            System.out.println("Enter msg: ");

            // String input
            String message = myObj.nextLine();

            UdpMessage messageToSend = new UdpMessage(1, 1, message, MessageType.Message);

            MessagesAck.put(messageToSend.getMessageUniqueId(), false);

            try {
                sendMessage(messageToSend, portToSend);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        t.start();
    }

    public MessageDeliveryTuple<UdpMessage, Integer> receiveMessage() throws IOException, ClassNotFoundException {
        return super.receiveMessage();
    }

    public void StartReceiveMessagesThread() {

        new Thread(() -> {
            while (true) {
                try {
                    MessageDeliveryTuple<UdpMessage,Integer> messageReceived = receiveMessage();

                    PrettyPrintMessage(messageReceived.getMessage());

                    processMessageReceived(messageReceived);

                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void processMessageReceived(MessageDeliveryTuple<UdpMessage, Integer> receivedTuple) throws IOException, NoSuchAlgorithmException, InterruptedException {
        UdpMessage msg = receivedTuple.getMessage();
        Integer portToSend = receivedTuple.getPort();

        switch (msg.type()) {

            case MessageType.Message -> {
                if (!isMessageDuplicate(msg)) {
                    ReceivedMessages.put(msg.getMessageUniqueId(), msg.message());
                    /// send echo response to sender
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    UdpMessage ackMessage = new UdpMessage(1, 1, "message ack", MessageType.Ack);
                    super.sendMessage(ackMessage, portToSend);
                }
            }

            case MessageType.Ack -> MessagesAck.put(msg.getMessageUniqueId(), true);

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
        return ReceivedMessages.get(msg.getMessageUniqueId()) != null;
    }

    //endregion


}
