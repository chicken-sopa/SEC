package Communication.Links.LinkMessages;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;

public record AckMessage(
        String message
) implements IMessage {
}
