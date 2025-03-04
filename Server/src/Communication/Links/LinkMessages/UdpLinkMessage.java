package Communication.Links.LinkMessages;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import Communication.Links.LinkMessages.Base.LinkMessage;
import Communication.Links.LinkMessages.Base.LinkMessageType;

public class UdpLinkMessage<T extends IMessage> extends LinkMessage<T> {

    public UdpLinkMessage(int senderId, int messageId, T message, LinkMessageType type){
        super(senderId, messageId, message, type);
    }
}



