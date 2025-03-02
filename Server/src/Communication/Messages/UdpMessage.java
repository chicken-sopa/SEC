package Communication.Messages;

import java.io.*;
import java.util.Arrays;

public class UdpMessage implements Serializable {

    private int senderId;
    private int messageId;
    private String message;
    private MessageType type;

    public UdpMessage(int senderId, int messageId, String message, MessageType type){
        this.senderId = senderId;
        this.messageId = messageId;
        this.message = message;
        this.type = type;
    }

    //Serialize message object in order to send trough UDP
    public byte[] serializeMessage() throws IOException {
        // Serialize to a byte array
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(this);
        oo.close();

        return bStream.toByteArray();
    }

    // Deserialize a byte array back into a Communication.Messages.UdpMessage object
    public static UdpMessage deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bStream = new ByteArrayInputStream(data);
             ObjectInputStream oi = new ObjectInputStream(bStream)) {
            return (UdpMessage) oi.readObject();
        }
    }

    public int getMessageUniqueId() {
        Integer[] messageUniqueIds = new Integer[2];
        messageUniqueIds[0] = senderId;
        messageUniqueIds[1] = messageId;
        return Arrays.hashCode(messageUniqueIds);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public int getSenderId() {
        return senderId;
    }
}



