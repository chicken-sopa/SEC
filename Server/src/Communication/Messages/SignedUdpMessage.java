package Communication.Messages;

import Communication.Messages.Base.IMessage;
import Communication.Messages.Base.SignedMessage;

public class SignedUdpMessage<T extends IMessage> extends SignedMessage<T> {

    public SignedUdpMessage(T msg, String signature) {
        super(msg, signature);
    }
}
