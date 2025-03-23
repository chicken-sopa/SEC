package com.sec.Messages;

import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;

import java.io.Serializable;

public class StateMessage extends BaseMessage implements Serializable {
    private final SignedValTSPair val;
    private final SignedWriteset writeset;

    public StateMessage(int senderId, SignedValTSPair val, SignedWriteset writeset, int consensusID) {
        super(MessageType.STATE, senderId, consensusID);
        this.val = val;
        this.writeset = writeset;
    }

    public SignedValTSPair getVal() {
        return val;
    }

    public SignedWriteset getWriteset() {
        return writeset;
    }

    @Override
    public String prettyPrint() {
        return super.prettyPrint() +
                "Val -> "+ (getVal() != null ? getVal().prettyPrint() : "null") +
                "|| Write SIZE -> "+ getWriteset().getWriteset().size();
    }


    //HACK TO TRY NOT HAVE CASTING ERRORS
    public BaseMessage toBaseMessage(){
        return new BaseMessage(MessageType.STATE, this.getSenderId(), this.getMsgConsensusID(), val, writeset);
    }
}
