package Communication.Links.LinkMessages.Base.Contracts;

import java.io.Serializable;

public interface IMessage extends Serializable {
    public Object message();
    public String prettyPrint();

}
