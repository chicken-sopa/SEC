package Client;

import Lib.ILib;
import Lib.Lib;
import com.sec.BlockChain.Transaction;
import Lib.ClientRequestManager;
import com.sec.Helpers.Constants;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.ConsensusFinishedMessage;
import com.sec.Messages.EvmResultMessage;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
                        if(clientRequests.updateEvmReceivedMessage() == fPlusOne)
                            System.out.println("F + 1 EVM Responses Received, operation confirmed ==> " + ((EvmResultMessage) msg).getVal());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    private Transaction ProcessCommands() {
        System.out.println("Address list:\n" +
                "BlockChainOwnerAddress : 0\n" +
                "AddressUser A : 1\n" +
                "AddressUser B : 2\n" +
                "AddressUser C : 3\n" +
                "OPERATION 1  : AddToBlackList(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 2  : RemoveToBlackList(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 3  : IsBlackListed(<ACCOUNT ADDRESS>)\n" +
                "OPERATION 4  : TransferISTCoins(<TO ACCOUNT ADDRESS>, <AMOUNT>)\n" +
                "OPERATION 5  : IncreaseAllowance(<SPENDER ADDRESS>, <AMOUNT TO ADD>)\n" +
                "OPERATION 6  : DecreaseAllowance(<SPENDER ADDRESS>, <AMOUNT TO DECREASE>)\n" +
                "OPERATION 7  : FetchMyISTCoinBalance()\n" +
                "OPERATION 8  : TransferDEPCoins(<TO ACCOUNT ADDRESS>, <AMOUNT>)\n" +
                "OPERATION 9  : FetchMyDEPCoinBalance()\n" +
                "OPERATION 10 : TransferISTCoinsFrom(<FROM ACCOUNT ADDRESS>, <TO ACCOUNT ADDRESS>, <AMOUNT>)\n" +
                "Type what to send in format: \"<OPERATION-NUMBER> <ARG 1> <ARG 2> <ARG 3> ...\"\n" +
                "When an address is to be inputted, please use numbers 0-3 according to the provided list of addresses");
        String input = sc.nextLine();
        String[] inputValues = input.split(" ");
        Transaction trans = null;
        String address;
        try {

            switch (Integer.parseInt(inputValues[0])) {
                case 1:
                    address = ParseAddressArgument(inputValues[1]);
                    if (address == null) return null;
                    trans = lib.AddToBlackList(myAddress, address);
                    break;
                case 2:
                    address = ParseAddressArgument(inputValues[1]);
                    if (address == null) return null;
                    trans = lib.RemoveFromBlackList(myAddress, address);
                    break;
                case 3:
                    address = ParseAddressArgument(inputValues[1]);
                    if (address == null) return null;
                    trans = lib.IsBlackListed(myAddress, address);
                    break;
                case 4:
                    try{
                        address = ParseAddressArgument(inputValues[1]);
                        if (address == null) return null;
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.TransferISTCoin(myAddress, myAddress, address, value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 5:
                    try{
                        address = ParseAddressArgument(inputValues[1]);
                        if (address == null) return null;
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.IncreaseAllowance(myAddress, address, value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 6:
                    try{
                        address = ParseAddressArgument(inputValues[1]);
                        if (address == null) return null;
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.DecreaseAllowance(myAddress, address, value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 7:
                    trans = lib.MyBalance(myAddress);
                    break;
                case 8:
                    try{
                        address = ParseAddressArgument(inputValues[1]);
                        if (address == null) return null;
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.TransferDepCoin(myAddress, address, value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                case 9:
                    trans = lib.MyDepCoinBalance(myAddress);
                    break;
                case 10:
                    try{
                        var addressFrom = ParseAddressArgument(inputValues[1]);
                        var addressTo = ParseAddressArgument(inputValues[2]);
                        if (addressFrom == null || addressTo == null) return null;
                        int value =  Integer.parseInt(inputValues[2]);
                        trans = lib.TransferISTCoin(myAddress, addressFrom, addressTo, value);
                    }catch (Exception e){
                        System.out.println("Inserted value is not valid");
                    }
                    break;
                default:
                    System.out.println("Value out of range (1-10)");
                    return null;
            }
            return trans;
        } catch (Exception e) {
            System.out.println("Provided arguments are incorrect.");
            return null;
        }
    }

    private String ParseAddressArgument(String addressArgument){
        var addressIndex = Integer.parseInt(addressArgument);
        if (addressIndex >= 0 && addressIndex <= Constants.allUserAddresses.length){
            return Constants.allUserAddresses[addressIndex];
        }
        return null;
    }

}
