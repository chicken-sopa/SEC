package com.sec.Links.LinkMessages;

import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.LinkMessages.Base.SignedLinkMessage;

public class SignedUdpLinkMessage<T extends IMessage> extends SignedLinkMessage<T> {

    public SignedUdpLinkMessage(ILinkMessage<T> msg, String signature) {
        super(msg, signature);
    }
}
