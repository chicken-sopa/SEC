package com.sec.Messages;

import com.sec.Messages.Types.ValTSPair.SignedValTSPair;

public class ConsensusFinishedMessage extends BaseMessage {
    String val;

    public ConsensusFinishedMessage(int senderID, int consensusID, String val) {
        super(MessageType.WRITE, senderID, consensusID);
        this.val = val;
    }
}
