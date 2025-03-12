package Lib.Links.LinkMessages;

import Lib.Links.LinkMessages.Base.Contracts.IMessage;
import Lib.Links.LinkMessages.Base.LinkMessage;
import Lib.Links.LinkMessages.Base.LinkMessageType;

public class UdpLinkMessage<T extends IMessage> extends LinkMessage<T> {

    public UdpLinkMessage(int senderId, int messageId, T message, LinkMessageType type){
        super(senderId, messageId, message, type);
    }
}



