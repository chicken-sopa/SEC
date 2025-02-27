import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/// ideas
/// server trys to send a message and records the message id sent
/// tries until its able to receive a message with ACK flag positive
/// if by a certain time it hasn't received an ack then it tries again
/// perfect link


public class UdpServer {


    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public UdpServer() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    public void run() throws IOException {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            //socket.receive(packet); socket.sent(packet)


            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            //packet = new DatagramPacket(buf, buf.length, address, port);
            //String received = new String(packet.getData(), 0, packet.getLength());

            /*if (received.equals("end")) {
                running = false;
                continue;
            }*/
            socket.send(packet);
        }
        socket.close();
    }

}
