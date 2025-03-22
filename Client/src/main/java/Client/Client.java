package Client;

import Lib.ILib;
import Lib.Lib;
import com.sec.Messages.ConsensusFinishedMessage;
//import com.sec.Messages.AbortMessage;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.MessageType;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {
    private final ILib lib;
    private int counter = 1;
    private final int id;
    private final Scanner sc;
    private final int[] destPorts;
    private final int fPlusOne = 2; // f+1 = 2 confirmations required

    public Client(int myPort, int myId, int[] destinationPorts) throws SocketException, NoSuchAlgorithmException {
        lib = new Lib(myPort);
        id = myId;
        sc = new Scanner(System.in);
        destPorts = destinationPorts;
    }

    private void sendMessage(String message) throws Exception {
        CountDownLatch latch = new CountDownLatch(fPlusOne);
        boolean[] aborted = {false};

        lib.addMessageListener(msg -> {
            System.out.println("--------------------Listener correu------------------------------");
            BaseMessage msg2 = null;
            System.out.println("condicao =" + (msg.getMessageType() == MessageType.FINISHED));

            if (msg.getMessageType() == MessageType.FINISHED) {
                 msg2 = (ConsensusFinishedMessage) msg;
            }//else if(msg.getMessageType() == MessageType.ABORT){ //TODO uncomment this when abort
//                AbortMessage msg2 = (AbortMessage) msg;
//            }
            if(msg2 != null) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Enterei no if");
                System.out.println("value enviado DO LADO DO LISTENER e" + message + "value recebido e " +((ConsensusFinishedMessage) msg2).getVal());

                if (((ConsensusFinishedMessage) msg2).getVal().equals(message)) {
                    System.out.println("Received one confirmation from " + msg2.getSenderId() + ": " + ((ConsensusFinishedMessage) msg2).getVal());
                    latch.countDown();

                }//TODO uncoment this for abort logic
//            else if (msg instanceof AbortMessage) {
//                System.out.println("Consensus aborted.");
//                aborted[0] = true;
//                latch.countDown(); // Ensure we stop waiting
//            }
            }
        });

        for (int destinationPort : destPorts) {
            lib.SendAppendMessage(message + counter++, destinationPort);
        }

        boolean consensusReached = latch.await(10, TimeUnit.SECONDS);

        if (aborted[0]) {
            System.out.println("Consensus aborted.");
        } else if (consensusReached) {
            System.out.println("Consensus reached! Value '" + message + "' confirmed.");
        } else {
            System.out.println("Consensus could not be reached within the timeout.");
        }
    }

    public void Listen() throws Exception {
        while (true) {
            System.out.println("Type message to send to servers:");
            String message = sc.nextLine();
            sendMessage(message);
        }
    }
}
