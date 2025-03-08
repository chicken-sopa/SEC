package Communication.Links;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import Communication.Links.LinkMessages.AckMessage;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Communication.Links.LinkMessages.Base.LinkMessageType;
import Communication.Links.LinkMessages.SignedUdpLinkMessage;
import Communication.Links.LinkMessages.UdpLinkMessage;
import Communication.Links.Security.DigitalSignatureAuth;
import Keys.KeyManager;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import static Configuration.ProcessConfig.getProcessId;

public class AuthenticatedPerfectLink<T extends IMessage> extends PerfectLink<T> {

    DigitalSignatureAuth<T> digitalSignatureAuth;
    private final PrivateKey privateKey = KeyManager.getPrivateKey();
    int messageIdCounter = 0;

    public AuthenticatedPerfectLink(int port, DigitalSignatureAuth<T> digitalSignatureAuth) throws SocketException, NoSuchAlgorithmException {
        super(port);
        this.digitalSignatureAuth = digitalSignatureAuth;
    }

    public void sendMessage(T msg, Integer portToSend) throws Exception {

        Thread t = new Thread(() -> {

            try {
                UdpLinkMessage<T> messageToSend = new UdpLinkMessage<>(getProcessId(), messageIdCounter++, msg, LinkMessageType.Message);

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
        boolean verified = digitalSignatureAuth.verifySignature(signedReceivedMessage.getMessage(), KeyManager.getNodePublicKey(processId), signedReceivedMessage.getSignature());
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

            case LinkMessageType.Message -> {
                /// send echo response to sender
                AckMessage ackMsg = new AckMessage(msg.getMessageUniqueId());
                UdpLinkMessage<T> ackMessage = new UdpLinkMessage<>(getProcessId(), messageIdCounter++, (T) ackMsg, LinkMessageType.Ack);
                String signature = digitalSignatureAuth.signMessage(ackMessage,privateKey);

                SignedUdpLinkMessage<T> signedAckMessage = new SignedUdpLinkMessage<>(ackMessage, signature);
                super.sendAckMessage(signedAckMessage, portToSend);
            }
            case LinkMessageType.Ack -> {
                AckMessage ack = (AckMessage) msg.getMessageValue();
                Integer receivedAckId = ack.message();
                MessagesAck.put(receivedAckId, true);
            }
        }
    }
}