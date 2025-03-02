package Communication.Messages;

import java.io.*;
import java.util.Arrays;

public record UdpMessage(
    int senderID,
    int messageID,
    String message,
    MessageType type
) implements Serializable {


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
        messageUniqueIds[0] = senderID;
        messageUniqueIds[1] = messageID;
        return Arrays.hashCode(messageUniqueIds);
    }

}



