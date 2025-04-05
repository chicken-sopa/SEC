package Lib;

import com.sec.BlockChain.Transaction;
import com.sec.Helpers.Constants;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Messages.AppendMessage;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.MessageType;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static Configuration.ClientConfig.getProcessId;

public class Lib implements ILib {
    private final AuthenticatedPerfectLink<BaseMessage> authenticatedPerfectLink;
    private final DigitalSignatureAuth<BaseMessage> digitalSignatureAuth;
    private final CopyOnWriteArrayList<Consumer<BaseMessage>> listeners = new CopyOnWriteArrayList<>();

    public Lib(int myPort) throws NoSuchAlgorithmException, SocketException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(myPort, digitalSignatureAuth, getProcessId());
    }

    public void SendAppendMessage(Transaction messageToAppend, int destinationPort) throws Exception {
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), messageToAppend, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    public BaseMessage ReceiveMessage() throws Exception{
        return  authenticatedPerfectLink.receiveMessage();
    }

    // Region BlackListCalls
    @Override
    public Transaction AddToBlackList(String fromAddress, String addressToAdd) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.addToBlacklistFunctionSignature, addressToAdd);
    }

    @Override
    public Transaction RemoveFromBlackList(String fromAddress, String addressToRemove) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.removeFromBlacklistFunctionSignature, addressToRemove);
    }

    @Override
    public Transaction IsBlackListed(String fromAddress, String blackListAddress) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.isBlackListedFunctionSignature, blackListAddress);
    }
    // End of BlackListCalls

    // Region ISTCoin Calls
    @Override
    public Transaction TransferISTCoin (String senderAddress, String fromAddress, String toAddress, int value) throws Exception {
        // TODO -> Add signature


        return CreateTransaction(Constants.ISTCoinContractAddress, senderAddress, "", "",
                Constants.transferFunctionSignature, fromAddress, toAddress, Integer.toHexString(value));
    }

    @Override
    public Transaction IncreaseAllowance(String fromAddress, String spenderAddress, int addedValue) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.increaseAllowanceFunctionSignature, spenderAddress, Integer.toHexString(addedValue));
    }

    @Override
    public Transaction DecreaseAllowance(String fromAddress, String spenderAddress, int subtractedValue) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.decreaseAllowanceFunctionSignature, spenderAddress, Integer.toHexString(subtractedValue));
    }

    @Override
    public Transaction Approve(String fromAddress, String spenderAddress, int amount) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.approveFunctionSignature, spenderAddress,Integer.toHexString(amount));
    }

    @Override
    public Transaction MyBalance(String fromAddress) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "", "",
                Constants.myBalanceFunctionSignature);
    }

    @Override
    public Transaction TransferDepCoin(String fromAddress, String toAddress, int value) throws Exception {
        return CreateTransaction(toAddress, fromAddress, Integer.toHexString(value), "");
    }

    @Override
    public Transaction MyDepCoinBalance(String fromAddress) throws Exception {
        return CreateTransaction("",  fromAddress, "", "");
    }
    // End of ISTCoin Calls

    private Transaction CreateTransaction(String destinationAddress, String fromAddress, String amount, String signature, String... args) throws Exception {
        return new Transaction(destinationAddress, fromAddress, args, amount,  getProcessId());
    }



    public void addMessageListener(Consumer<BaseMessage> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(BaseMessage message) {
        for (Consumer<BaseMessage> listener : listeners) {
            listener.accept(message);
        }
    }
}
