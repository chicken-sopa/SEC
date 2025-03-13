package com.sec.Links;

import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.LinkMessages.AckMessage;
import com.sec.Links.Data.MessageDeliveryTuple;
import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.LinkMessageType;
import com.sec.Links.LinkMessages.SignedUdpLinkMessage;
import com.sec.Links.LinkMessages.UdpLinkMessage;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Keys.KeyManager;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicInteger;



public class AuthenticatedPerfectLink<T extends IMessage> extends PerfectLink<T> {

    DigitalSignatureAuth<T> digitalSignatureAuth;
    private  PrivateKey privateKey;
    AtomicInteger messageIdCounter = new AtomicInteger(0);
    Integer id;

    public AuthenticatedPerfectLink(int port, DigitalSignatureAuth<T> digitalSignatureAuth, Integer id) throws SocketException, NoSuchAlgorithmException {
        super(port);
        this.digitalSignatureAuth = digitalSignatureAuth;
        this.id=id;
        privateKey = KeyManager.getPrivateKey(id);
    }

    public void sendMessage(T msg, Integer portToSend) throws Exception {

        Thread t = new Thread(() -> {

            try {
                UdpLinkMessage<T> messageToSend = new UdpLinkMessage<>(id, messageIdCounter.getAndAdd(1), msg, LinkMessageType.Message);

                String signature = digitalSignatureAuth.signMessage(messageToSend, privateKey);

                SignedUdpLinkMessage<T> authenticatedMessage = new SignedUdpLinkMessage<>(messageToSend, signature);

                super.sendMessage(authenticatedMessage, portToSend);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();

    }

    public T receiveMessage() throws Exception {
        MessageDeliveryTuple<ILinkMessage<T>, Integer> messageTuple = receiveLinkMessage();
        if (messageTuple == null)
            return null;
        return messageTuple.getMessage().getMessageValue();
    }

    protected MessageDeliveryTuple<ILinkMessage<T>, Integer> receiveLinkMessage() throws Exception {
        MessageDeliveryTuple<ILinkMessage<T>, Integer> messageReceived = super.receiveLinkMessage();
        if (messageReceived == null) {
            return null;
        }
        var signedReceivedMessage = (SignedUdpLinkMessage<T>) messageReceived.getMessage();
        int processId = signedReceivedMessage.getSenderId();
        boolean verified = digitalSignatureAuth.verifySignature(signedReceivedMessage.getMessage(), KeyManager.getPublicKey(processId), signedReceivedMessage.getSignature());
        if (!verified) {
            return null;
        }
            processMessageReceived(messageReceived);


        if (messageReceived.getMessage().getType() == LinkMessageType.Ack)
            return null;
        return messageReceived;
    }


    @SuppressWarnings("unchecked")
    public void processMessageReceived(MessageDeliveryTuple<ILinkMessage<T>, Integer> receivedMsg) throws Exception {
        ILinkMessage<T> msg = receivedMsg.getMessage();
        Integer portToSend = receivedMsg.getPort();
        switch (msg.getType()) {

            case Message -> {
                /// send echo response to sender
                AckMessage ackMsg = new AckMessage(msg.getMessageUniqueId());
                UdpLinkMessage<T> ackMessage = new UdpLinkMessage<>(id, messageIdCounter.getAndAdd(1), (T) ackMsg, LinkMessageType.Ack);
                String signature = digitalSignatureAuth.signMessage(ackMessage,privateKey);

                SignedUdpLinkMessage<T> signedAckMessage = new SignedUdpLinkMessage<>(ackMessage, signature);
                super.sendAckMessage(signedAckMessage, portToSend);
            }
            case Ack -> {
                AckMessage ack = (AckMessage) msg.getMessageValue();
                Integer receivedAckId = ack.message();
                MessagesAck.put(receivedAckId, true);
            }
        }
    }
}