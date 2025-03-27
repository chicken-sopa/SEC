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

    // Region BlackListCalls
    @Override
    public void AddToBlackList(String fromAddress, String addressToAdd, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.BlackListContractAddress, fromAddress, "", Constants.addToBlacklistFunctionSignature, addressToAdd);
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    @Override
    public void RemoveToBlackList(String fromAddress, String addressToRemove, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.BlackListContractAddress, fromAddress, "",
                Constants.removeFromBlacklistFunctionSignature, addressToRemove);
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    @Override
    public void IsBlackListed(String fromAddress, String blackListAddress, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.BlackListContractAddress, fromAddress, "",
                Constants.isBlackListedFunctionSignature, blackListAddress);
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }
    // End of BlackListCalls

    // Region ISTCoin Calls
    @Override
    public void Transfer(String fromAddress, String toAddress, int value, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, fromAddress, toAddress, String.valueOf(value));
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    @Override
    public void IncreaseAllowance(String fromAddress, String spenderAddress, int addedValue, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, spenderAddress, String.valueOf(addedValue));
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    @Override
    public void DecreaseAllowance(String fromAddress, String spenderAddress, int subtractedValue, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, spenderAddress, String.valueOf(subtractedValue));
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    @Override
    public void Approve(String fromAddress, String spenderAddress, int amount, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.addToBlacklistFunctionSignature, spenderAddress, String.valueOf(amount));
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    @Override
    public void MyBalance(String fromAddress, int destinationPort) throws Exception {
        // TODO -> Add signature
        Transaction transaction = CreateTransaction(Constants.ISTCoinContractAddress, fromAddress, "",
                Constants.myBalanceFunctionSignature);
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), transaction, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
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
