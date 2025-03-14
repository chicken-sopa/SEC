package com.sec.Messages;

import com.sec.Messages.Types.ValTSPair.SignedValTSPair;

public class AcceptMessage extends BaseMessage{


    SignedValTSPair pairToProposeAccept;

    public AcceptMessage(int senderId, SignedValTSPair pairToProposeAccept, int consensusID ) {
        super(MessageType.ACCEPT, senderId, consensusID );
        this.pairToProposeAccept = pairToProposeAccept;
    }


    public com.sec.Messages.Types.ValTSPair.SignedValTSPair getPairToProposeAccept() {
        return pairToProposeAccept;
    }
}