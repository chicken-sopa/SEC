package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.Base.IMessage;
import Communication.Messages.Base.Message;
import Communication.Messages.MessageType;
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
        if (!MessagesAck.containsKey(msg.getMessageUniqueId())){
            MessagesAck.put(msg.getMessageUniqueId(), false);
        }
        while (!MessagesAck.get(msg.getMessageUniqueId())) {
            try {
                super.sendMessage(msg, portToSend);
                Thread.sleep(500);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendAckMessage(T msg, Integer portToSend) throws Exception {
        try {
            super.sendMessage(msg, portToSend);
        } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageDeliveryTuple<T, Integer> receiveMessage() throws Exception {

        MessageDeliveryTuple<T, Integer> receivedMsg = super.receiveMessage();
        T msg = receivedMsg.getMessage();

        if (msg.getType() == MessageType.Ack){
            return receivedMsg;
        }
        else if(!isMessageDuplicate(msg)) {
            ReceivedMessages.put(msg.getMessageUniqueId(), msg.getMessageValue());
            return receivedMsg;
        }
        return null;
    }

    private boolean isMessageDuplicate(T msg) {
        return ReceivedMessages.get(msg.getMessageUniqueId()) != null;
    }

    @SuppressWarnings("unchecked")
    public void processMessageReceived(MessageDeliveryTuple<T, Integer> receivedMsg) throws Exception {
        T msg = receivedMsg.getMessage();
        Integer portToSend = receivedMsg.getPort();

        switch (msg.getType()) {

            case MessageType.Message -> {
                /// send echo response to sender
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                UdpMessage ackMessage = new UdpMessage(1, 1, "message ack", MessageType.Ack);
                super.sendMessage((T) ackMessage, portToSend);
            }

            case MessageType.Ack -> MessagesAck.put(msg.getMessageUniqueId(), true);
        }
    }


}
