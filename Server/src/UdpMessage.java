public record UdpMessage(
    int senderID,
    int messageID,
    String message,
    MessageType type
){}



enum MessageType{
    Ack,
    Message,
    /*Init,
    Propose,
    Decide
     */

}

