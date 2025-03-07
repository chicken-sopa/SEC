package Communication.Collection;

public class InitConsensusMessage extends BaseMessage{


    public BaseMessage(int epochId) {
        this.epochId = epochId;
        this.message = "INIT COLLECT" + Integer.toString(epochId);
    }

    public InitConsensusMessage(int epochId, String message) {
        super(epochId, message);
    }
}
