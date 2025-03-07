package Communication.Collection;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import java.io.Serializable;

public abstract class BaseMessage implements IMessage, Serializable {
    private final String messageType;
    private final int senderId; // Optional: to record which node sent the message

    public BaseMessage(String messageType, int senderId) {
        this.messageType = messageType;
        this.senderId = senderId;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getSenderId() {
        return senderId;
    }

    // For simple messages, we return 'this' as the payload.
    @Override
    public Object message() {
        return this;
    }
}
