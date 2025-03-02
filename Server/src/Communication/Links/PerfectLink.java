package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.Base.IMessage;
import Communication.Messages.UdpMessage;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink<T extends IMessage> extends FairLossLink<T> {

    ConcurrentHashMap<Integer, String> ReceivedMessages = new ConcurrentHashMap<Integer, String>();
    ConcurrentHashMap<Integer, Boolean> MessagesAck = new ConcurrentHashMap<Integer, Boolean>();


    public PerfectLink(int port) throws SocketException {
        super(port);
    }


    public void sendMessage(T msg, Integer portToSend) throws Exception {
        while(!MessagesAck.get(msg.getMessageUniqueId())){
            try{
                super.sendMessage(msg, portToSend);
                Thread.sleep(100);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public void startSendMessageThread() {
//        Thread t = new Thread(() -> {
//            Scanner myObj = new Scanner(System.in);
//
//            Integer portToSend = 4555;
//
//            System.out.println("Enter msg: ");
//
//            // String input
//            String message = myObj.nextLine();
//
//            UdpMessage messageToSend = new UdpMessage(1, 1, message, MessageType.Message);
//
//            MessagesAck.put(messageToSend.getMessageUniqueId(), false);
//
//            try {
//                sendMessage(messageToSend, portToSend);
//            } catch (IOException | NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        t.start();
//    }

    public MessageDeliveryTuple<T, Integer> receiveMessage() throws IOException, ClassNotFoundException {
        return super.receiveMessage();
    }

//    public void StartReceiveMessagesThread() {
//
//        new Thread(() -> {
//            while (true) {
//                try {
//                    MessageDeliveryTuple<UdpMessage,Integer> messageReceived = receiveMessage();
//
//                    PrettyPrintMessage(messageReceived.getMessage());
//
//                    processMessageReceived(messageReceived);
//
//                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//    }

//    private void processMessageReceived(MessageDeliveryTuple<T, Integer> receivedTuple) throws IOException, NoSuchAlgorithmException, InterruptedException {
//        T msg = receivedTuple.getMessage();
//        Integer portToSend = receivedTuple.getPort();
//
//        switch (msg.getType()) {
//
//            case MessageType.Message -> {
//                if (!isMessageDuplicate(msg)) {
//                    ReceivedMessages.put(msg.getMessageUniqueId(), msg.getMessage());
//                    /// send echo response to sender
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    T ackMessage = new Message<T>();
//                    super.sendMessage(ackMessage, portToSend);
//                }
//            }
//
//            case MessageType.Ack -> MessagesAck.put(msg.getMessageUniqueId(), true);
//
//        }
//    }

    //region Auxiliary methods

    private void PrettyPrintMessage(UdpMessage msg){
        System.out.println("message received");
        System.out.println("sendID: " + msg.getSenderId() +
                " || msgID: " + msg.getMessageId() +
                " ||  msg: " + msg.getMessageValue() +
                " || type: " + msg.getType()
        );
    }

    private boolean isMessageDuplicate(T msg) {
        return ReceivedMessages.get(msg.getMessageUniqueId()) != null;
    }

    //endregion


}
