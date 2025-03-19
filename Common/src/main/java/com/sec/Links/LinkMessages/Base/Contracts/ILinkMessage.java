package com.sec.Links.LinkMessages.Base.Contracts;

import com.sec.Links.LinkMessages.Base.LinkMessageType;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public interface ILinkMessage<T extends IMessage> extends Serializable {

    int getMessageUniqueId();
    byte[] serializeMessage() throws IOException;

    int getSenderId();
    UUID getMessageId();
    T getMessageValue();
    LinkMessageType getType();
}
