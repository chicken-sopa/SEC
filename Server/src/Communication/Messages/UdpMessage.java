package Communication.Messages;

import Communication.Messages.Base.Message;

public class UdpMessage extends Message {

    public UdpMessage(int senderId, int messageId, String message, MessageType type){
        super(senderId, messageId, message, type);
    }
}



