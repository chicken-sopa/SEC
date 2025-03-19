package com.sec.Links.LinkMessages.Base;

import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.LinkMessages.UdpLinkMessage;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public abstract class LinkMessage<T extends IMessage> implements ILinkMessage<T>{

    private final Integer senderId;
    private final UUID messageId;
    private final T message;
    private final LinkMessageType type;

    public LinkMessage(int senderId, UUID messageId, T message, LinkMessageType type){
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
        String[] messageUniqueIds = new String[2];
        messageUniqueIds[0] = senderId.toString();
        messageUniqueIds[1] = messageId.toString();

        return Arrays.hashCode(messageUniqueIds);
    }

    @Override
    public UUID getMessageId() {
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
