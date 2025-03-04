//package Communication.Collection;
//
//import Communication.Links.Base.LinkMessages.LinkMessageType;
//import Communication.Links.Contracts.Base.LinkMessages.ILinkMessage;
//
///**
// * Message used for the Conditional Collect abstraction.
// */
//public class CollectLinkMessage implements ILinkMessage {
//
//    private final int epochId;
//
//    public CollectLinkMessage(int epochId) {
//        this.epochId = epochId;
//    }
//
//    public int getEpochId() {
//        return epochId;
//    }
//
//
//
//
//
//    @Override
//    public LinkMessageType getType() {
//        return 0; // Define an appropriate type
//    }
//
//    @Override
//    public String getMessageUniqueId() {
//        return String.valueOf(epochId);
//    }
//}
