package com.sec.Links.LinkMessages.Base.Contracts;

import com.sec.Links.LinkMessages.Base.LinkMessageType;

import java.io.IOException;
import java.io.Serializable;

public interface ILinkMessage<T extends IMessage> extends Serializable {

    int getMessageUniqueId();
    byte[] serializeMessage() throws IOException;

    int getSenderId();
    int getMessageId();
    T getMessageValue();
    LinkMessageType getType();
}
