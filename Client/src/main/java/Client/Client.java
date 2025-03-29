package Client;

import Lib.ILib;
import Lib.Lib;
import com.sec.BlockChain.Transaction;
import Lib.ClientRequestManager;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {
    private final ILib lib;
    private int counter = 1;
    private final int id;
    private final Scanner sc;
    private final int[] destPorts;
    private final int fPlusOne = 2; // f+1 = 2 confirmations required
    private final String myAddress;

    public Client(int myPort, int myId, int[] destinationPorts, String myAddress) throws SocketException, NoSuchAlgorithmException {
        lib = new Lib(myPort);
        id = myId;
        sc = new Scanner(System.in);
        destPorts = destinationPorts;
        this.myAddress = myAddress;
    }

    private void sendMessage(Transaction message) throws Exception {
        /*CountDownLatch latch = new CountDownLatch(fPlusOne);
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
        });*/

        for (int destinationPort : destPorts) {
            lib.SendAppendMessage(message, destinationPort);
        }

        /*boolean consensusReached = latch.await(10, TimeUnit.SECONDS);

        if (aborted[0]) {
            System.out.println("Consensus aborted.");
        } else if (consensusReached) {
            System.out.println("Consensus reached! Value '" + message + "' confirmed.");
        } else {
            System.out.println("Consensus could not be reached within the timeout.");
        }*/
    }

    public void SendRequestToConsensus() throws Exception {
        while (true) {
            ClientRequestManager clientRequests = new ClientRequestManager(lib, destPorts);
            Transaction message = ProcessCommands();
            clientRequests.sendMessage(message);
            clientRequests.waitForResponses();
        }
    }

    private Transaction ProcessCommands() {
        System.out.println("OPERATION 1  : AddToBlackList(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 2  : RemoveToBlackList(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 3  : IsBlackListed(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 4  : Transfer(<FROM ACCOUNT ADDRESS>, <TO ACCOUNT ADDRESS>, <AMOUNT>)\n" +
                "OPERATION 5  : IncreaseAllowance(<SPENDER ADDRESS>, <AMOUNT TO ADD>)\n" +
                "OPERATION 6  : DecreaseAllowance(<SPENDER ADDRESS>, <AMOUNT TO DECREASE>)\n" +
                "OPERATION 7  : Approve(<SPENDER ADDRESS>, <AMOUNT TO APPROVE>)\n" +
                "OPERATION 8  : FetchMyBalance()\n" +
                "Type what to send in format: \"<OPERATION-NUMBER> <ARG 1> <ARG 2> <ARG 3> ...\"" +
                "Please type addresses WITHOUT the hex prefix \"0x\"");
        String input = sc.nextLine();
        String[] inputValues = input.split(" ");
        Transaction trans;
        try{

            switch (Integer.parseInt(inputValues[0])){
                case 1:
                    trans = lib.AddToBlackList(myAddress, inputValues[1]);
                    break;
                case 2:
                    trans = lib.RemoveFromBlackList(myAddress, inputValues[1]);
                    break;
                case 3:
                    trans = lib.IsBlackListed(myAddress, inputValues[1]);
                    break;
                case 4:
                    trans = lib.Transfer(myAddress, inputValues[1], Integer.parseInt(inputValues[2]));
                    break;
                case 5:
                    trans = lib.IncreaseAllowance(myAddress, inputValues[1], Integer.parseInt(inputValues[2]));
                    break;
                case 6:
                    trans = lib.DecreaseAllowance(myAddress,  inputValues[1], Integer.parseInt(inputValues[2]));
                    break;
                case 7:
                    trans = lib.Approve(myAddress, inputValues[1], Integer.parseInt(inputValues[2]));
                    break;
                case 8:
                    trans = lib.MyBalance(myAddress);
                    break;
                default:
                    System.out.println("Value out of range (1-8)");
                    return null;
            }
            return trans;
        }
        catch(Exception e){
            System.out.println("Provided arguments are incorrect.");
            return null;
        }
    }
}
