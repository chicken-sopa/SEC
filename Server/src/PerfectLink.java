import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink extends FairLossLink {

     ConcurrentHashMap<int, String>  ReceivedMessages;

    public PerfectLink() throws SocketException {

    }

    @Override
    public void sendMessage(String msg) throws IOException, NoSuchAlgorithmException {
        super.sendMessage(msg);
    }

    @Override
    public void receiveMessage() throws IOException {
        super.receiveMessage();
    }


    public void sendThread(){
        Thread t = new Thread(()->{
                UdpMessage msg = new UdpMessage()
            while (true){

            }

        });

        t.start();
    }

}
