package com.sec.Messages;

import com.sec.BlockChain.Transaction;

public class ConsensusFinishedMessage extends BaseMessage {
    Transaction val;

    public ConsensusFinishedMessage(int senderID, int consensusID, Transaction val) {
        super(MessageType.FINISHED, senderID, consensusID);
        this.val = val;
    }

    public Transaction getVal() {
        return val;
    }
}
