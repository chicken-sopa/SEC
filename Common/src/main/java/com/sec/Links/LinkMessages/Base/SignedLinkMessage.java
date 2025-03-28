package com.sec.Links.LinkMessages.Base;




import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.LinkMessages.Base.Contracts.ISignedLinkMessage;

import java.io.*;
import java.util.UUID;

public abstract class SignedLinkMessage<T extends IMessage> implements ISignedLinkMessage<T> {

    private final String signature;
    private final ILinkMessage<T> message;

    public SignedLinkMessage(ILinkMessage<T> msg, String signature){
        message = msg;
        this.signature = signature;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public ILinkMessage<T> getMessage() {
        return message;
    }

    @Override
    public int getMessageUniqueId() {
        return message.getMessageUniqueId();
    }

    @Override
    public byte[] serializeMessage() throws IOException {
        // Serialize to a byte array
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(this);
        oo.close();
        return bStream.toByteArray();
    }

    @Override
    public int getSenderId() {
        return message.getSenderId();
    }

    @Override
    public UUID getMessageId() {
        return message.getMessageId();
    }

    @Override
    public T getMessageValue(){
        return message.getMessageValue();
    }

    @Override
    public LinkMessageType getType() {
        return message.getType();
    }
}
