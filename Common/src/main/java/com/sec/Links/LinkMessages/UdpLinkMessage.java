package com.sec.Links.LinkMessages;

import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.LinkMessages.Base.LinkMessage;
import com.sec.Links.LinkMessages.Base.LinkMessageType;

public class UdpLinkMessage<T extends IMessage> extends LinkMessage<T> {

    public UdpLinkMessage(int senderId, int messageId, T message, LinkMessageType type){
        super(senderId, messageId, message, type);
    }
}



