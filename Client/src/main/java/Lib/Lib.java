package Lib;

import com.sec.BlockChain.Transaction;
import com.sec.Helpers.Constants;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Messages.AppendMessage;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.ConsensusFinishedMessage;
//import com.sec.Messages.AbortMessage;
import com.sec.Messages.MessageType;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
        startReceiveMessageThread();
    }

    public void SendAppendMessage(Transaction messageToAppend, int destinationPort) throws Exception {
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), messageToAppend, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    // Region BlackListCalls
    @Override
    public Transaction AddToBlackList(String fromAddress, String addressToAdd) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.BlackListContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, addressToAdd);
    }

    @Override
    public Transaction RemoveFromBlackList(String fromAddress, String addressToRemove) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.BlackListContractAddress, fromAddress, "",
                Constants.removeFromBlacklistFunctionSignature, addressToRemove);
    }

    @Override
    public Transaction IsBlackListed(String fromAddress, String blackListAddress) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.BlackListContractAddress, fromAddress, "",
                Constants.isBlackListedFunctionSignature, blackListAddress);
    }
    // End of BlackListCalls

    // Region ISTCoin Calls
    @Override
    public Transaction Transfer(String fromAddress, String toAddress, int value) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, fromAddress, toAddress, String.valueOf(value));
    }

    @Override
    public Transaction IncreaseAllowance(String fromAddress, String spenderAddress, int addedValue) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, spenderAddress, String.valueOf(addedValue));
    }

    @Override
    public Transaction DecreaseAllowance(String fromAddress, String spenderAddress, int subtractedValue) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, spenderAddress, String.valueOf(subtractedValue));
    }

    @Override
    public Transaction Approve(String fromAddress, String spenderAddress, int amount) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, spenderAddress, String.valueOf(amount));
    }

    @Override
    public Transaction MyBalance(String fromAddress) throws Exception {
        // TODO -> Add signature
        return CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.myBalanceFunctionSignature);
    }
    // End of ISTCoin Calls

    private Transaction CreateTransaction(String destinationContract, String fromAddress, String signature, String... args) throws Exception {
        return new Transaction(destinationContract, fromAddress, args , signature);
    }

    private void startReceiveMessageThread() {
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage msg = authenticatedPerfectLink.receiveMessage();
                    if (msg != null && msg instanceof ConsensusFinishedMessage) {
                        System.out.println("-------------------- VIM DAR NOTIFY AO SLIETERNS ---------------------");
                        System.out.println("MESSAGE RECEBIDA Da lib e" + msg.getMessageType());
                        notifyListeners(msg);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
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
