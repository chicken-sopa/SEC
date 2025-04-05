package com.sec.Messages;

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


    static public String prettyPrintCollectedMSg(CollectedMessage collectedMessage){
        StringBuilder msg = new StringBuilder();
        msg.append("---------------VALUES IN COLLECTED MESSAGE: \n");
        for(StateMessage stateMessage : collectedMessage.getCollectedStates().values()){
            msg.append(stateMessage.prettyPrint());
            msg.append("\n");
        }
        msg.append("-------------------------");
        return msg.toString();
    }
}
