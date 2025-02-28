import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.security.*;
/// ideas
/// server trys to send a message and records the message id sent
/// tries until its able to receive a message with ACK flag positive
/// if by a certain time it hasn't received an ack then it tries again
/// perfect link


public class FairLossLink {


    private final DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];

    public FairLossLink() throws SocketException {
        socket = new DatagramSocket(4445);
    }


    public void sendMessage(String msg) throws IOException, NoSuchAlgorithmException {
        buffer = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 4445);

        socket.send(packet);
        String sent = new String(packet.getData(), 0, packet.getLength());
        System.out.println("sent " + sent);

    }

    public void receiveMessage() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("received " + received);

    }




}


