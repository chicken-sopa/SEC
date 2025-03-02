package Communication.Links;

import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Messages.Base.IMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

/// ideas
/// server trys to send a message and records the message id sent
/// tries until its able to receive a message with ACK flag positive
/// if by a certain time it hasn't received an ack then it tries again
/// perfect link


public class FairLossLink<T extends IMessage> {


    private final DatagramSocket socket;
    private boolean running;
    //private byte[] buffer = new byte[256];

    public FairLossLink(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }


    public void sendMessage(T msg, Integer portToSend) throws Exception {

        byte[] buffer = msg.serializeMessage();
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), portToSend);

        socket.send(packet);
        //String sent = new String(packet.getData(), 0, packet.getLength());
        System.out.println("message sent");
        System.out.println("sendID: " + msg.getSenderId() + " || msgID: "+ msg.getMessageId() + " ||  msg: " + msg.getMessageValue());
    }

    public MessageDeliveryTuple<T, Integer> receiveMessage() throws Exception {
        byte[] buffer = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);
        byte[] receivedData = packet.getData();

        // Deserialize the byte array into a Communication.Messages.UdpMessage object
        return new MessageDeliveryTuple<>(deserializeMessage(receivedData), packet.getPort());
    }

    protected T  deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(bStream)) {
            return (T) objectInputStream.readObject();
        }
    }


}


