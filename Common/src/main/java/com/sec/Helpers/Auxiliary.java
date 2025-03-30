package com.sec.Helpers;

import  com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;

public class Auxiliary {
    private Auxiliary() {}

    @SuppressWarnings("rawtypes")
    public static void PrettyPrintUdpMessageReceived(ILinkMessage msg){
        System.out.println("message received => senderID: " + msg.getSenderId() +
                " || msgID: " + msg.getMessageId() +
                " ||  msg: " + msg.getMessageValue().prettyPrint() +
                " || LinkType: " + msg.getType()
        );
    }

    @SuppressWarnings("rawtypes")
    public static void PrettyPrintUdpMessageSent(ILinkMessage msg, int processToReceive){
        System.out.println("message sent => senderID: " + msg.getSenderId() +
                " || msgID: " + msg.getMessageId() +
                " ||  msg: " + msg.getMessageValue().prettyPrint() +
                " || LinkType: " + msg.getType() +
                " || receiverID: " + processToReceive
        );
    }
}
