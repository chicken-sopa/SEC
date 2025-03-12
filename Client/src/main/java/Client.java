import Lib.ILib;
import Lib.Lib;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {

    Scanner sc;
    ILib lib;

    public Client(int myPort) throws SocketException, NoSuchAlgorithmException {
        sc = new Scanner(System.in);
        lib = new Lib(myPort);
    }
}
