import Lib.ILib;
import Lib.Lib;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {

    Scanner sc;
    ILib lib;
    int counter = 1;

    public Client(int myPort) throws SocketException, NoSuchAlgorithmException {
        sc = new Scanner(System.in);
        lib = new Lib(myPort);
    }

    public void sendSomething() throws Exception {
        System.out.println("Type destination port:");
        int destinationPort = sc.nextInt();
        lib.SendAppendMessage("MessageFromClient-" + counter++, destinationPort);
    }


}
