package Communication.Links;

import Communication.Links.LinkMessages.Base.Contracts.IMessage;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Links.LinkMessages.Base.Contracts.ILinkMessage;

import java.io.ByteArrayInputStream;
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

        socket.send(packet);
    }

    protected MessageDeliveryTuple<ILinkMessage<T>, Integer> receiveLinkMessage() throws Exception {

        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        byte[] receivedData = packet.getData();

        return new MessageDeliveryTuple<>(deserializeMessage(receivedData), packet.getPort());
    }

    @SuppressWarnings("unchecked")
    protected ILinkMessage<T>  deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(bStream)) {
            return (ILinkMessage<T>) objectInputStream.readObject();
        }
    }


}


