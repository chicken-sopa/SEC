package Client;

import Lib.ILib;
import Lib.Lib;
import com.sec.BlockChain.Transaction;
import Lib.ClientRequestManager;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.ConsensusFinishedMessage;
import com.sec.Messages.EvmResultMessage;

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
    ClientRequestManager clientRequests;


    public Client(int myPort, int myId, int[] destinationPorts, String myAddress) throws SocketException, NoSuchAlgorithmException {
        lib = new Lib(myPort);
        id = myId;
        sc = new Scanner(System.in);
        destPorts = destinationPorts;
        this.myAddress = myAddress;
        clientRequests = new ClientRequestManager(lib, destPorts);
    }

    public void SendRequestToConsensusThread() throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    Transaction message = ProcessCommands();
                    if(message != null) {
                        clientRequests.sendMessage(message);
                        clientRequests.waitForResponses();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void startReceiveMessageThread() {
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage msg = lib.ReceiveMessage();
                    if (msg instanceof ConsensusFinishedMessage) {

                        Transaction transaction = ((ConsensusFinishedMessage) msg).getVal();
                        clientRequests.updateOnMessageCountReceivedMessage(transaction);

                    } else if (msg instanceof EvmResultMessage) {
                        System.out.println("EVM Response Reveived ==> " + ((EvmResultMessage) msg).getVal());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    private Transaction ProcessCommands() {
        System.out.println("OPERATION 1  : AddToBlackList(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 2  : RemoveToBlackList(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 3  : IsBlackListed(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 4  : TransferISTCoins(<TO ACCOUNT ADDRESS>, <AMOUNT>)\n" +
                "OPERATION 5  : IncreaseAllowance(<SPENDER ADDRESS>, <AMOUNT TO ADD>)\n" +
                "OPERATION 6  : DecreaseAllowance(<SPENDER ADDRESS>, <AMOUNT TO DECREASE>)\n" +
                "OPERATION 7  : Approve(<SPENDER ADDRESS>, <AMOUNT TO APPROVE>)\n" +
                "OPERATION 8  : FetchMyISTCoinBalance()\n" +
                "OPERATION 9  : TransferDEPCoins(<TO ACCOUNT ADDRESS>, <AMOUNT>)\n" +
                "OPERATION 10 : FetchMyDEPCoinBalance()\n" +
                "Type what to send in format: \"<OPERATION-NUMBER> <ARG 1> <ARG 2> <ARG 3> ...\"" +
                "Please type addresses WITHOUT the hex prefix \"0x\"");
        String input = sc.nextLine();
        String[] inputValues = input.split(" ");
        Transaction trans = null;
        try {

            switch (Integer.parseInt(inputValues[0])) {
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
                    try{
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.TransferISTCoin(myAddress, inputValues[1], value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 5:
                    try{
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.IncreaseAllowance(myAddress, inputValues[1], value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 6:
                    try{
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.DecreaseAllowance(myAddress, inputValues[1], value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 7:
                    try{
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.Approve(myAddress, inputValues[1], value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 8:
                    trans = lib.MyBalance(myAddress);
                    break;
                case 9:
                    try{
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.TransferDepCoin(myAddress, inputValues[1], value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 10:
                    trans = lib.MyDepCoinBalance(myAddress);
                    break;
                default:
                    System.out.println("Value out of range (1-8)");
                    return null;
            }
            return trans;
        } catch (Exception e) {
            System.out.println("Provided arguments are incorrect.");
            return null;
        }
    }
}
