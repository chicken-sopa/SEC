package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.UdpMessage;

import java.io.IOException;
import java.net.*;
import java.security.*;
/// ideas
/// server trys to send a message and records the message id sent
/// tries until its able to receive a message with ACK flag positive
/// if by a certain time it hasn't received an ack then it tries again
/// perfect link


public class FairLossLink {


    private final DatagramSocket socket;
    private boolean running;
    //private byte[] buffer = new byte[256];

    public FairLossLink(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }


    public void sendMessage(UdpMessage msg, Integer portToSend) throws IOException, NoSuchAlgorithmException {

        byte[] buffer = msg.serializeMessage();
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), portToSend);

        socket.send(packet);
        String sent = new String(packet.getData(), 0, packet.getLength());
        System.out.println("message sent");
        System.out.println("sendID: " + msg.getSenderId() + " || msgID: "+ msg.getMessageId() + " ||  msg: " + msg.getMessage());


    }

    public MessageDeliveryTuple<UdpMessage, Integer> receiveMessage() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);


        socket.receive(packet);
        byte[] receivedData = packet.getData();


        // Deserialize the byte array into a Communication.Messages.UdpMessage object
        return new MessageDeliveryTuple<>(UdpMessage.deserializeMessage(receivedData), packet.getPort());


    }




}


