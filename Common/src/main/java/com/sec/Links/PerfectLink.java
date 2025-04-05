package com.sec.Links;

import com.sec.Helpers.Auxiliary;
import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.Data.MessageDeliveryTuple;
import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.LinkMessageType;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink<T extends IMessage> extends FairLossLink<T> {
    final int NumberAttemptsThreshold = 10;
    ConcurrentHashMap<Integer, IMessage> ReceivedMessages = new ConcurrentHashMap<Integer, IMessage>();
    ConcurrentHashMap<Integer, Boolean> MessagesAck = new ConcurrentHashMap<Integer, Boolean>();
    ConcurrentHashMap<Integer, Integer> NumberAttemptsMessagesAck = new ConcurrentHashMap<Integer, Integer>();


    public PerfectLink(int port) throws SocketException {
        super(port);
    }

    public void sendMessage(ILinkMessage<T> msg, Integer portToSend) throws Exception {
        if (!MessagesAck.containsKey(msg.getMessageUniqueId())){
            MessagesAck.put(msg.getMessageUniqueId(), false);
        }
        while (!MessagesAck.get(msg.getMessageUniqueId())  && NumberAttemptsMessagesAck.getOrDefault(msg.getMessageUniqueId(), 0) < NumberAttemptsThreshold ) {
            try {
                super.sendMessage(msg, portToSend);
                int processIdToReceive = (portToSend - 4550);
                Auxiliary.PrettyPrintUdpMessageSent(msg,  processIdToReceive);
                NumberAttemptsMessagesAck.put(msg.getMessageUniqueId(), NumberAttemptsMessagesAck.getOrDefault(msg.getMessageUniqueId(), 0) + 1);
                Thread.sleep(500);
            } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendAckMessage(ILinkMessage<T> msg, Integer portToSend) throws Exception {
        try {
            super.sendMessage(msg, portToSend);
            int processIdToReceive = (portToSend - 4550);
            Auxiliary.PrettyPrintUdpMessageSent(msg, processIdToReceive);
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
        System.out.println("THIS IS MESSAGE IS DUPLICATE SO IGNORING");
        return null;
    }

    private boolean isMessageDuplicate(ILinkMessage<T> msg) {
        return ReceivedMessages.get(msg.getMessageUniqueId()) != null;
    }
}
