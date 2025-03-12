package Communication.Collection;

import Communication.Messages.BaseMessage;
import Communication.Messages.MessageType;
import Communication.Messages.StateMessage;

import java.util.Map;

public class CollectedMessage extends BaseMessage {
    public Map<Integer, StateMessage> getCollectedStates() {
        return collectedStates;
    }

    Map<Integer, StateMessage>  collectedStates;
    public CollectedMessage(int senderId ,Map<Integer, StateMessage>  collectedMessages) {
        super(MessageType.COLLECTED, senderId );
        this.collectedStates = collectedMessages;

    }
}
