package com.sec.Messages;

import com.sec.BlockChain.Transaction;

public class AbortedConsensusMessage extends BaseMessage {

    Transaction val;

    public AbortedConsensusMessage(int senderID, int consensusID, Transaction val) {
        super(MessageType.FINISHED, senderID, consensusID);
        this.val = val;
    }

    public Transaction getVal() {
        return val;
    }
}


