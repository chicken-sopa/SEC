package Communication.Messages;

public class AppendMessage extends BaseMessage{

    private final String message;
    public AppendMessage(MessageType messageType, int senderId, String stringToAppend) {
        super(messageType, senderId);
        message = stringToAppend;
    }

    public String getMessage() {
        return message;
    }
}
