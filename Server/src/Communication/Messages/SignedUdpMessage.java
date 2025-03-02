package Communication.Messages;

public class SignedUdpMessage {

    private UdpMessage message;
    private String signature;

    public SignedUdpMessage(UdpMessage msg,  String signature) {
        message =  msg;
        this.signature = signature;
    }
}
