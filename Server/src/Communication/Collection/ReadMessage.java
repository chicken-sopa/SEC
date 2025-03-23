package Communication.Collection;

import  com.sec.Messages.BaseMessage;
import  com.sec.Messages.MessageType;

public class ReadMessage extends BaseMessage {
    public ReadMessage(int senderId, int currentConsensusID) {
        super(MessageType.READ, senderId,  currentConsensusID);
    }
}
