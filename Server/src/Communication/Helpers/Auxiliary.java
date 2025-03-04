package Communication.Helpers;

import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;

public class Auxiliary {
    private Auxiliary() {}

    @SuppressWarnings("rawtypes")
    public static void PrettyPrintUdpMessageReceived(ILinkMessage msg){
        System.out.println("message received => sendID: " + msg.getSenderId() +
                " || msgID: " + msg.getMessageId() +
                " ||  msg: " + msg.getMessageValue() +
                " || type: " + msg.getType()
        );
    }

    @SuppressWarnings("rawtypes")
    public static void PrettyPrintUdpMessageSent(ILinkMessage msg){
        System.out.println("message sent => sendID: " + msg.getSenderId() +
                " || msgID: " + msg.getMessageId() +
                " ||  msg: " + msg.getMessageValue() +
                " || type: " + msg.getType()
        );
    }
}
