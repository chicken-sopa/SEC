package Lib.Links.LinkMessages;

import Lib.Links.LinkMessages.Base.Contracts.ILinkMessage;
import Lib.Links.LinkMessages.Base.Contracts.IMessage;
import Lib.Links.LinkMessages.Base.SignedLinkMessage;

public class SignedUdpLinkMessage<T extends IMessage> extends SignedLinkMessage<T> {

    public SignedUdpLinkMessage(ILinkMessage<T> msg, String signature) {
        super(msg, signature);
    }
}
