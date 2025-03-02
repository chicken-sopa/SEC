import Communication.Links.PerfectLink;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Server server = new Server(4555);

        server.init();
    }
}