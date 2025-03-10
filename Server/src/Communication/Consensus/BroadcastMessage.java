package Communication.Consensus;

import Communication.Collection.BaseMessage;
import Communication.Collection.InitCollectMessage;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.PerfectLink;
import Communication.Links.Security.DigitalSignatureAuth;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.BackingStoreException;

public class BroadcastMessage<T extends BaseMessage> {

    private final AuthenticatedPerfectLink<T> link;
    private final int quorumSize;

    public BroadcastMessage(AuthenticatedPerfectLink<T> link, int quorumSize) throws SocketException, NoSuchAlgorithmException {
        this.link = (AuthenticatedPerfectLink<T>) link;
        this.quorumSize = quorumSize;


    }


    public void sendBroadcast(int ts, T msgRequest) throws Exception {
        for (int i = 0;  i <= 2 ;i++) {
            link.sendMessage(msgRequest, 4550 + i);
        }
    }

}
