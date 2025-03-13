package Communication.Messages;

public class AppendMessage extends BaseMessage{

    private final String message;
    public AppendMessage(MessageType messageType, int senderId, String stringToAppend, int consensusID) {
        super(messageType, senderId, consensusID);
        message = stringToAppend;
    }

    public String getMessage() {
        return message;
    }
}
