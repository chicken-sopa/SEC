package com.sec.Messages;

import com.sec.BlockChain.Transaction;

public class EvmResultMessage extends BaseMessage {
    String val;

    public EvmResultMessage(int senderID, String val) {
        super(MessageType.EVMRESULT, senderID, 0);
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}