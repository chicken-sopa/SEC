package Communication.Collection;

public class InitCollectMessage extends BaseMessage {
    private final long timestamp;

    public InitCollectMessage(int senderId) {
        super("INIT COLLECT", senderId);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String prettyPrint() {
        return "Init Collect Message";
    }
}
