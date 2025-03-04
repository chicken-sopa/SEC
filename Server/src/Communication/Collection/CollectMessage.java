package Communication.Collection;

import Communication.Messages.MessageType;
import Communication.Messages.Base.IMessage;
import java.security.PublicKey;

/**
 * Message used for the Conditional Collect abstraction.
 */
public class CollectMessage implements IMessage {

    private final int epochId;

    public CollectMessage(int epochId) {
        this.epochId = epochId;
    }

    public int getEpochId() {
        return epochId;
    }





    @Override
    public MessageType getType() {
        return 0; // Define an appropriate type
    }

    @Override
    public String getMessageUniqueId() {
        return String.valueOf(epochId);
    }
}
