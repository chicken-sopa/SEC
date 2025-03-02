package Communication.Messages.Base;

import Communication.Messages.MessageType;
import Communication.Messages.UdpMessage;

import java.io.*;
import java.util.Arrays;

public abstract class Message implements IMessage, Serializable{

    private final int senderId;
    private final int messageId;
    private final String message;
    private final MessageType type;

    public Message(int senderId, int messageId, String message, MessageType type){
        this.senderId = senderId;
        this.messageId = messageId;
        this.message = message;
        this.type = type;
    }

    // Deserialize a byte array back into a Communication.Messages.UdpMessage object
    public UdpMessage deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bStream = new ByteArrayInputStream(data);
             ObjectInputStream oi = new ObjectInputStream(bStream)) {
            return (UdpMessage) oi.readObject();
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
    public String getMessageValue() {
        return message;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public int getSenderId() {
        return senderId;
    }
}
