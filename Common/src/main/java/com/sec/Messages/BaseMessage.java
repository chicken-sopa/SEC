package com.sec.Messages;

import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;

import java.io.Serializable;

public class BaseMessage implements IMessage, Serializable {
    protected final MessageType messageType;
    private final int senderId; // Optional: to record which node sent the message

    private final int consensusID;

    private SignedValTSPair val = null;
    private SignedWriteset writeset = null;

    public BaseMessage(MessageType messageType, int senderId, int consensusID) {
        this.messageType = messageType;
        this.senderId = senderId;
        this.consensusID = consensusID;
    }

    public BaseMessage(MessageType messageType, int senderId, int consensusID, SignedValTSPair val, SignedWriteset writeset) {
        this.messageType = messageType;
        this.senderId = senderId;
        this.consensusID = consensusID;
        this.val = val;
        this.writeset = writeset;
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
        return "Type("+ messageType+") ";
    }

    public int getMsgConsensusID() {return this.consensusID;}

    public StateMessage toStateMessage(){
        return new StateMessage(this.senderId, val, writeset, consensusID);
    }
}
