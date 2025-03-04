package Communication.Links;

import Communication.Helpers.Auxiliary;
import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import Communication.Links.LinkMessages.AckMessage;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Communication.Links.LinkMessages.Base.LinkMessageType;
import Communication.Links.LinkMessages.UdpLinkMessage;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink<T extends IMessage> extends FairLossLink<T> {

    ConcurrentHashMap<Integer, IMessage> ReceivedMessages = new ConcurrentHashMap<Integer, IMessage>();
    ConcurrentHashMap<Integer, Boolean> MessagesAck = new ConcurrentHashMap<Integer, Boolean>();


    public PerfectLink(int port) throws SocketException {
        super(port);
    }


    public void sendMessage(ILinkMessage<T> msg, Integer portToSend) throws Exception {
        if (!MessagesAck.containsKey(msg.getMessageUniqueId())){
            MessagesAck.put(msg.getMessageUniqueId(), false);
        }
        while (!MessagesAck.get(msg.getMessageUniqueId())) {
            try {
                super.sendMessage(msg, portToSend);
                Auxiliary.PrettyPrintUdpMessageSent(msg);
                Thread.sleep(500);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendAckMessage(ILinkMessage<T> msg, Integer portToSend) throws Exception {
        try {
            super.sendMessage(msg, portToSend);
            Auxiliary.PrettyPrintUdpMessageSent(msg);
        } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageDeliveryTuple<ILinkMessage<T>, Integer> receiveMessage() throws Exception {

        MessageDeliveryTuple<ILinkMessage<T>, Integer> receivedMsg = super.receiveMessage();
        Auxiliary.PrettyPrintUdpMessageReceived(receivedMsg.getMessage());
        ILinkMessage<T> msg = receivedMsg.getMessage();

        if (msg.getType() == LinkMessageType.Ack){
            return receivedMsg;
        }
        else if(!isMessageDuplicate(msg)) {
            ReceivedMessages.put(msg.getMessageUniqueId(), msg.getMessageValue());
            return receivedMsg;
        }
        return null;
    }

    private boolean isMessageDuplicate(ILinkMessage<T> msg) {
        return ReceivedMessages.get(msg.getMessageUniqueId()) != null;
    }

    @SuppressWarnings("unchecked")
    public void processMessageReceived(MessageDeliveryTuple<ILinkMessage<T>, Integer> receivedMsg) throws Exception {
        ILinkMessage<T> msg = receivedMsg.getMessage();
        Integer portToSend = receivedMsg.getPort();

        switch (msg.getType()) {

            case LinkMessageType.Message -> {
                /// send echo response to sender
                AckMessage ackMsg = new AckMessage("message ack");
                UdpLinkMessage<T> ackMessage = new UdpLinkMessage<>(1, 1, (T) ackMsg, LinkMessageType.Ack);
                super.sendMessage(ackMessage, portToSend);
            }

            case LinkMessageType.Ack -> MessagesAck.put(msg.getMessageUniqueId(), true);
        }
    }


}
