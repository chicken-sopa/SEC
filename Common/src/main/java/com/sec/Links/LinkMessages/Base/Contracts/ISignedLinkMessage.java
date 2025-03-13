package com.sec.Links.LinkMessages.Base.Contracts;

public interface ISignedLinkMessage<T extends IMessage> extends ILinkMessage<T> {

    public String getSignature();
    public ILinkMessage<T> getMessage();
}
