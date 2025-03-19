package Client;

import Lib.ILib;
import Lib.Lib;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {

    ILib lib;
    int counter = 1;
    int Id;
    Scanner sc;
    int[] destPorts;

    public Client(int myPort, int myId, int[] destinationPorts) throws SocketException, NoSuchAlgorithmException {
        lib = new Lib(myPort);
        Id = myId;
        sc = new Scanner(System.in);
        destPorts = destinationPorts;
    }

    private void sendMessage(String message) throws Exception {
        for (int destinationPort : destPorts) {
            lib.SendAppendMessage(message + counter++, destinationPort);
            //TODO: wait for f+1 replies confirming that a value was written
        }

    }

    public void Listen() throws Exception {
        System.out.println("Type message1 to send to servers:");
        String message = sc.nextLine();
//            System.out.println("Message length: " + message.length());
//            System.out.println("Hex dump: " + message.chars()
//                .mapToObj(c -> String.format("%02X ", c))
//                .reduce("", String::concat));
        sendMessage("message 1");
        while (true) {
            System.out.println("Type message to send to servers:");
            String message2 = sc.nextLine();
            System.out.println("THE MESSAGE SENT IS == " + message2 );
            sendMessage(message2);
        }
    }

}
