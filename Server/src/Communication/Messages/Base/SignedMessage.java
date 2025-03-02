package Communication.Messages.Base;



import Communication.Messages.MessageType;

import java.io.*;

public abstract class SignedMessage<T extends IMessage> implements ISignedMessage, Serializable {

    private final String signature;
    private final T message;

    public SignedMessage(T msg, String signature){
        message = msg;
        this.signature = signature;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public T getMessage() {
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
    public int getMessageId() {
        return message.getMessageId();
    }

    @Override
    public String getMessageValue(){
        return message.getMessageValue();
    }

    @Override
    public MessageType getType() {
        return message.getType();
    }
}
