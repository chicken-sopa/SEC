package Communication.Collection;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;

/**
 * Message used for the Conditional Collect abstraction.
 */
public class BaseMessage implements IMessage {

    private final int epochId;
    private final String message;

    public CollectMessage(int epochId) {
        this.epochId = epochId;
        this.message = "INIT COLLECT" + Integer.toString(epochId);
    }

    public CollectMessage(int epochId, String message) {
        this.epochId = epochId;
        this.message = message;
    }

    public int getEpochId() {
        return epochId;
    }

    @Override
    public String message() {
        return message;
    }
}
