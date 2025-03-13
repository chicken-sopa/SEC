package Communication.Collection;

import  com.sec.Messages.BaseMessage;
import  com.sec.Messages.MessageType;
import  com.sec.Messages.StateMessage;

import java.util.Map;

public class CollectedMessage extends BaseMessage {
    public Map<Integer, StateMessage> getCollectedStates() {
        return collectedStates;
    }

    Map<Integer, StateMessage>  collectedStates;
    public CollectedMessage(int senderId ,Map<Integer, StateMessage>  collectedMessages, int currentConsensusID) {
        super(MessageType.COLLECTED, senderId,currentConsensusID );
        this.collectedStates = collectedMessages;
    }
}
