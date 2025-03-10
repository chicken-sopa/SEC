package Communication.Collection;

import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.Writeset.SignedWriteset;

import java.util.Map;

public class CollectedMessage extends BaseMessage{
    Map<Integer, StateMessage>  collectedStates;
    public CollectedMessage(int senderId ,Map<Integer, StateMessage>  collectedMessages) {
        super(MessageType.COLLECTED, senderId );
        this.collectedStates = collectedMessages;

    }
}
