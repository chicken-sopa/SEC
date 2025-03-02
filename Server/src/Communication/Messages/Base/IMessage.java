package Communication.Messages.Base;

import Communication.Messages.MessageType;

import java.io.IOException;

public interface IMessage {

    int getMessageUniqueId();
    byte[] serializeMessage() throws IOException;

    int getSenderId();
    int getMessageId();
    String getMessageValue();
    MessageType getType();
}
