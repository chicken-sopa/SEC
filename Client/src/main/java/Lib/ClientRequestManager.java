package Lib;

import com.sec.BlockChain.Transaction;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ClientRequestManager {

    private final ILib lib;
    private final int[] destPorts;
    private final int fPlusOne = 2; // f+1 = 2 confirmations required

    private int currentTransaction;

    private final AtomicInteger numberAnswersReceived = new AtomicInteger(0);

    private final AtomicInteger numberEVMAnwsers = new AtomicInteger(0);

    private Boolean consensusAborted = false;


    public ClientRequestManager(ILib lib, int[] destPorts) {
        this.lib = lib;
        this.destPorts = destPorts;
    }

    public void resetManagerForNewMessage(Transaction newMessage) {
        currentTransaction = newMessage.hashCode();
        numberAnswersReceived.set(0);
        numberEVMAnwsers.set(0);
        consensusAborted = false;
    }

    public void sendMessage(Transaction message) throws Exception {
        resetManagerForNewMessage(message);
        for (int destinationPort : destPorts) {
            lib.SendAppendMessage(message, destinationPort);
        }
    }


    public void waitForResponses() {
        while (numberAnswersReceived.get() < fPlusOne && !consensusAborted) {

            /*ReentrantLock lock;
            lock.wait();
            lock.no*/
        }
        if (consensusAborted) {
            System.out.println("------------------CONSENSUS WAS ABORTED---------------------------------");
        } else {
            System.out.println("Received confirmation that transaction has beena agreed upon from f+1 processes.");
        }
    }


    public void updateOnMessageCountReceivedMessage(Transaction message) {
        if (Objects.hashCode(message) == currentTransaction) {
            numberAnswersReceived.incrementAndGet();
        }
    }

    public int updateEvmReceivedMessage() {
        return numberEVMAnwsers.incrementAndGet();

    }

    public void clientRequestAborted(){
        this.consensusAborted = true;
    }
}
