package Communication.Consensus;

import  com.sec.Messages.BaseMessage;
import  com.sec.Links.AuthenticatedPerfectLink;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class BroadcastMessage<T extends BaseMessage> {

    private final AuthenticatedPerfectLink<T> link;
    private final int quorumSize;

    public BroadcastMessage(AuthenticatedPerfectLink<T> link, int quorumSize) throws SocketException, NoSuchAlgorithmException {
        this.link = (AuthenticatedPerfectLink<T>) link;
        this.quorumSize = quorumSize;
    }


    public void sendBroadcast(T msgRequest) throws Exception {
        for (int i = 0;  i <= 2 ;i++) {
            link.sendMessage(msgRequest, 4550 + i);
        }
    }

}
