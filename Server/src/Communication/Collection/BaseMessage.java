package Communication.Collection;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;

/**
 * Message used for the Conditional Collect abstraction.
 */
public class BaseMessage implements IMessage {

    private final int epochId;
    private final Object message;


    public BaseMessage(int epochId, String message) {
        this.epochId = epochId;
        this.message = message;
    }

    public int getEpochId() {
        return epochId;
    }

    @Override
    public Object message() {
        return message;
    }
}
