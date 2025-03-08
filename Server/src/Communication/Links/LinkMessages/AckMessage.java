package Communication.Links.LinkMessages;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;

public record AckMessage(
        Integer message //message id that was acked

) implements IMessage {
    @Override
    public String prettyPrint() {
        return String.valueOf(message);
    }
}
