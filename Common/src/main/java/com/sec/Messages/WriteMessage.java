package com.sec.Messages;

import com.sec.Messages.Types.ValTSPair.SignedValTSPair;

public class WriteMessage extends BaseMessage{
     SignedValTSPair pairToProposeWrite;

    public WriteMessage(int senderId, SignedValTSPair pairToProposeWrite, int consensusID ) {
        super(MessageType.WRITE, senderId, consensusID );
        this.pairToProposeWrite = pairToProposeWrite;
    }

    public com.sec.Messages.Types.ValTSPair.SignedValTSPair getPairToProposeWrite() {
        return pairToProposeWrite;
    }
}


