package com.sec.Links;

import com.sec.Links.LinkMessages.Base.Contracts.IMessage;
import com.sec.Links.Data.MessageDeliveryTuple;
import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class FairLossLink<T extends IMessage> {


    private final DatagramSocket socket;

    public FairLossLink(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }


    public void sendMessage(ILinkMessage<T> msg, Integer portToSend) throws Exception {

        byte[] buffer = msg.serializeMessage();
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), portToSend);
        System.out.println("Sending data of length: " + packet.getLength());
        socket.send(packet);
    }

    protected MessageDeliveryTuple<ILinkMessage<T>, Integer> receiveLinkMessage() throws Exception {
        byte[] buffer = new byte[8192];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        // Use packet.getLength() to get the valid data
        byte[] receivedData = new byte[packet.getLength()];
        System.arraycopy(buffer, 0, receivedData, 0, packet.getLength());


        return new MessageDeliveryTuple<>(deserializeMessage(receivedData), packet.getPort());
    }


    @SuppressWarnings("unchecked")
    protected ILinkMessage<T> deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {

        if (data == null || data.length == 0) {
            System.out.println("Received an empty datagram.");
            return null;
        }
        try (ByteArrayInputStream bStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(bStream)) {
            return (ILinkMessage<T>) objectInputStream.readObject();
        } catch (EOFException eof) {
            System.out.println("Reached unexpected end of stream during deserialization.");
            return null;
        }
    }



}


