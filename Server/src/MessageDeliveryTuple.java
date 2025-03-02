public class MessageDeliveryTuple<UdpMessage, Integer> extends Tuple<UdpMessage, Integer> {

    public MessageDeliveryTuple(UdpMessage message, Integer port) {

        super(message, port);
    }

    public UdpMessage getMessage(){
        return x;
    }

    public Integer getPort(){
        return y;
    }

}
