package Communication.Helpers;

import Communication.Messages.Base.IMessage;

public class Auxiliary {
    private Auxiliary() {}

    public static void PrettyPrintUdpMessage(IMessage msg){
        System.out.println("message received");
        System.out.println("sendID: " + msg.getSenderId() +
                " || msgID: " + msg.getMessageId() +
                " ||  msg: " + msg.getMessageValue() +
                " || type: " + msg.getType()
        );
    }
}
