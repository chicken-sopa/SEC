package Communication.Collection;

import Communication.Collection.BaseMessage;
import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.Writeset.SignedWriteset;

import java.io.Serializable;

public class StateMessage extends BaseMessage implements Serializable {
    private final SignedValTSPair val;
    private final SignedWriteset writeset;

    public StateMessage(int senderId, SignedValTSPair val, SignedWriteset writeset) {
        super(MessageType.STATE, senderId);
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
                "Val -> "+ getVal().toString() +
                " Write -> "+ getWriteset().toString();
    }
}
