package Communication.Collection;

import Communication.Collection.BaseMessage;
import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.Writeset.SignedWriteset;

public class StateMessage extends BaseMessage {
    private final SignedValTSPair val;
    private final SignedWriteset writeset;

    public StateMessage(int senderId, SignedValTSPair val, SignedWriteset writeset) {
        super("STATE", senderId);
        this.val = val;
        this.writeset = writeset;
    }

    public SignedValTSPair getVal() {
        return val;
    }

    public SignedWriteset getWriteset() {
        return writeset;
    }
}
