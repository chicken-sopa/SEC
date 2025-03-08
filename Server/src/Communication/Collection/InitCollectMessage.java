package Communication.Collection;

public class InitCollectMessage extends BaseMessage {
    private final long timestamp;

    public InitCollectMessage(int senderId) {
        super(MessageType.INIT_COLLECT, senderId);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
