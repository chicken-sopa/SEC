package Communication.Collection;

import  com.sec.Messages.BaseMessage;
import  com.sec.Messages.MessageType;

public class InitCollectMessage extends BaseMessage {
    public InitCollectMessage(int senderId, int currentConsensusID) {
        super(MessageType.INIT_COLLECT, senderId,  currentConsensusID);
    }
}
