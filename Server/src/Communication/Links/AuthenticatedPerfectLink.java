package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.Base.IMessage;
import Communication.Messages.SignedUdpMessage;
import Communication.Security.DigitalSignatureAuth;

import java.io.IOException;
import java.net.SocketException;
import java.security.KeyPair;

public class AuthenticatedPerfectLink<T extends IMessage> extends PerfectLink<T>{

    DigitalSignatureAuth<T> digitalSignatureAuth ;

    public AuthenticatedPerfectLink(int port, DigitalSignatureAuth<T> digitalSignatureAuth ) throws SocketException {
        super(port);
        this.digitalSignatureAuth = digitalSignatureAuth;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendMessage(T msg, Integer portToSend) throws Exception {

        KeyPair keyPair = digitalSignatureAuth.generateKeyPair();

        String signature = digitalSignatureAuth.signMessage(msg, keyPair.getPrivate());

        SignedUdpMessage<T> authenticatedMessage = new SignedUdpMessage<T>(msg, signature);

        super.sendMessage((T) authenticatedMessage, portToSend);
    }

    public MessageDeliveryTuple receiveValidatedMessage() throws IOException, ClassNotFoundException {

        SignedMessageDeliveryTuple messageReceived = super.receiveMessage();

        return messageReceived.convertToMessageDeliveryTuple();

    }
}