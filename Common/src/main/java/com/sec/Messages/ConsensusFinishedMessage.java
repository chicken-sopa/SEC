package com.sec.Messages;

public class ConsensusFinishedMessage extends BaseMessage {
    String val;

    public ConsensusFinishedMessage(int senderID, int consensusID, String val) {
        super(MessageType.FINISHED, senderID, consensusID);
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
