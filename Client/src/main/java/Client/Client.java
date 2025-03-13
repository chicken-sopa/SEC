package Client;

import Lib.ILib;
import Lib.Lib;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class Client {

    ILib lib;
    int counter = 1;
    int Id;

    public Client(int myPort, int myId) throws SocketException, NoSuchAlgorithmException {
        lib = new Lib(myPort);
        Id = myId;
    }

    public void sendSomething(int[] portList) throws Exception {
        for(int destinationPort : portList) {
            lib.SendAppendMessage("MessageFromClient-" + counter++, destinationPort);
        }

    }
}
