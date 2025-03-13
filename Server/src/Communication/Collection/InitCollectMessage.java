package Communication.Collection;

import Communication.Messages.BaseMessage;
import Communication.Messages.MessageType;

public class InitCollectMessage extends BaseMessage {
    public InitCollectMessage(int senderId, int currentConsensusID) {
        super(MessageType.INIT_COLLECT, senderId,  currentConsensusID);
    }
}
