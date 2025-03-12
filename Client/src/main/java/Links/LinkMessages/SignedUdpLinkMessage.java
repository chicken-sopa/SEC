package Links.LinkMessages;

import Links.LinkMessages.Base.Contracts.ILinkMessage;
import Links.LinkMessages.Base.Contracts.IMessage;
import Links.LinkMessages.Base.SignedLinkMessage;

public class SignedUdpLinkMessage<T extends IMessage> extends SignedLinkMessage<T> {

    public SignedUdpLinkMessage(ILinkMessage<T> msg, String signature) {
        super(msg, signature);
    }
}
