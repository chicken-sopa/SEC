package Lib.Links;

import Lib.Helpers.Auxiliary;
import Lib.Links.Data.MessageDeliveryTuple;
import Lib.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Lib.Links.LinkMessages.Base.Contracts.IMessage;
import Lib.Links.LinkMessages.Base.LinkMessageType;

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

    protected MessageDeliveryTuple<ILinkMessage<T>, Integer> receiveLinkMessage() throws Exception {

        MessageDeliveryTuple<ILinkMessage<T>, Integer> receivedMsg = super.receiveLinkMessage();
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
}
