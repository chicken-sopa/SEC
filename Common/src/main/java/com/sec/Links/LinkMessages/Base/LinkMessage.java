package com.sec.Links.LinkMessages.Base;

import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.LinkMessages.UdpLinkMessage;

import java.io.*;
import java.util.Arrays;

public abstract class LinkMessage<T extends IMessage> implements ILinkMessage<T>{

    private final int senderId;
    private final int messageId;
    private final T message;
    private final LinkMessageType type;

    public LinkMessage(int senderId, int messageId, T message, LinkMessageType type){
        this.senderId = senderId;
        this.messageId = messageId;
        this.message = message;
        this.type = type;
    }

    // Deserialize a byte array back into a Communication.LinkMessages.UdpMessage object
    public UdpLinkMessage<T> deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bStream = new ByteArrayInputStream(data);
             ObjectInputStream oi = new ObjectInputStream(bStream)) {
            return (UdpLinkMessage<T>) oi.readObject();
        }
    }

    //Serialize message object in order to send trough UDP
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
    public int getMessageUniqueId() {
        Integer[] messageUniqueIds = new Integer[2];
        messageUniqueIds[0] = senderId;
        messageUniqueIds[1] = messageId;
        return Arrays.hashCode(messageUniqueIds);
    }

    @Override
    public int getMessageId() {
        return messageId;
    }

    @Override
    public T getMessageValue() {
        return message;
    }

    @Override
    public LinkMessageType getType() {
        return type;
    }

    @Override
    public int getSenderId() {
        return senderId;
    }
}
