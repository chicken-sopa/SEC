package Communication.Messages.Base;

public interface ISignedMessage extends IMessage{

    public String getSignature();
    public IMessage getMessage();
}
