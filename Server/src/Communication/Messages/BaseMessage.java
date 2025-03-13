package Communication.Messages;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import java.io.Serializable;

public abstract class BaseMessage implements IMessage, Serializable {
    protected final MessageType messageType;
    private final int senderId; // Optional: to record which node sent the message

    private final int consensusID;

    public BaseMessage(MessageType messageType, int senderId, int consensusID) {
        this.messageType = messageType;
        this.senderId = senderId;
        this.consensusID = consensusID;
    }

    public MessageType getMessageType() {
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

    @Override
    public String prettyPrint(){
        return "Type("+ messageType+")";
    }

    public int getMsgConsensusID() {return this.consensusID;}
}
