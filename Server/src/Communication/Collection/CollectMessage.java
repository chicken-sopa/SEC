package Communication.Collection;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;

/**
 * Message used for the Conditional Collect abstraction.
 */
public class CollectMessage implements IMessage {

    private final int epochId;
    private final String strToAppend;

    public CollectMessage(int epochId, String strToAppend) {
        this.epochId = epochId;
        this.strToAppend = strToAppend;
    }

    public int getEpochId() {
        return epochId;
    }

    @Override
    public String message() {
        return strToAppend;
    }
}
