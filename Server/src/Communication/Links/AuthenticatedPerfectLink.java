package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.Base.IMessage;
import Communication.Messages.MessageType;
import Communication.Messages.SignedUdpMessage;
import Communication.Messages.UdpMessage;
import Communication.Security.DigitalSignatureAuth;

import java.io.IOException;
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

    @Override
    @SuppressWarnings("unchecked")
    public void sendMessage(T msg, Integer portToSend) throws Exception {

        String signature = digitalSignatureAuth.signMessage(msg, keyPair.getPrivate());

        SignedUdpMessage<T> authenticatedMessage = new SignedUdpMessage<T>(msg, signature);

        super.sendMessage((T) authenticatedMessage, portToSend);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MessageDeliveryTuple<T, Integer> receiveMessage() throws Exception {

        MessageDeliveryTuple<T, Integer> messageReceived = super.receiveMessage();
        if (messageReceived == null)
            return null;
        var signedReceivedMessage = (SignedUdpMessage<T>) messageReceived.getMessage();

        boolean verified = digitalSignatureAuth.verifySignature(signedReceivedMessage.getMessage(), keyPair.getPublic(), signedReceivedMessage.getSignature());
        if (!verified)
            return null;
        return messageReceived;

    }

    @Override
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
                String signature = digitalSignatureAuth.signMessage((T)ackMessage, keyPair.getPrivate());

                SignedUdpMessage<T> signedAckMessage = new SignedUdpMessage<T>((T) ackMessage, signature);
                super.sendAckMessage((T) signedAckMessage, portToSend);
            }

            case MessageType.Ack -> MessagesAck.put(msg.getMessageUniqueId(), true);
        }
    }
}