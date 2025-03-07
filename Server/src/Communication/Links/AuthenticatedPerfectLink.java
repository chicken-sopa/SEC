package Communication.Links;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import Communication.Links.LinkMessages.AckMessage;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Communication.Links.LinkMessages.Base.LinkMessageType;
import Communication.Links.LinkMessages.SignedUdpLinkMessage;
import Communication.Links.LinkMessages.UdpLinkMessage;
import Communication.Links.Security.DigitalSignatureAuth;

import java.net.SocketException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class AuthenticatedPerfectLink<T extends IMessage> extends PerfectLink<T> {

    DigitalSignatureAuth<T> digitalSignatureAuth;
    KeyPair keyPair;

    public AuthenticatedPerfectLink(int port, DigitalSignatureAuth<T> digitalSignatureAuth) throws SocketException, NoSuchAlgorithmException {
        super(port);
        this.digitalSignatureAuth = digitalSignatureAuth;
        keyPair = this.digitalSignatureAuth.generateKeyPair();
    }

    public void sendMessage(T msg, Integer portToSend) throws Exception {

        Thread t = new Thread(() -> {

            try {
                UdpLinkMessage<T> messageToSend = new UdpLinkMessage<>(1, 1, msg, LinkMessageType.Message);

                String signature = digitalSignatureAuth.signMessage(messageToSend, keyPair.getPrivate());

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
        if (messageReceived == null)
            return null;
        var signedReceivedMessage = (SignedUdpLinkMessage<T>) messageReceived.getMessage();

        boolean verified = digitalSignatureAuth.verifySignature(signedReceivedMessage.getMessage(), keyPair.getPublic(), signedReceivedMessage.getSignature());
        if (!verified)
            return null;
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

            case LinkMessageType.Message -> {
                /// send echo response to sender
                AckMessage ackMsg = new AckMessage("message ack");
                UdpLinkMessage<T> ackMessage = new UdpLinkMessage<>(1, 1, (T) ackMsg, LinkMessageType.Ack);
                String signature = digitalSignatureAuth.signMessage(ackMessage, keyPair.getPrivate());

                SignedUdpLinkMessage<T> signedAckMessage = new SignedUdpLinkMessage<>(ackMessage, signature);
                super.sendAckMessage(signedAckMessage, portToSend);
            }

            case LinkMessageType.Ack -> MessagesAck.put(msg.getMessageUniqueId(), true);
        }
    }
}