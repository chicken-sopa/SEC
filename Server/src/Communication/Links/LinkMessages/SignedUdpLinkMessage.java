package Communication.Links.LinkMessages;

import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import Communication.Links.LinkMessages.Base.SignedLinkMessage;

public class SignedUdpLinkMessage<T extends IMessage> extends SignedLinkMessage<T> {

    public SignedUdpLinkMessage(ILinkMessage<T> msg, String signature) {
        super(msg, signature);
    }
}
