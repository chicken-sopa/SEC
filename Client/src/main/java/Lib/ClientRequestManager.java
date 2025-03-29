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

    private AtomicInteger numberAnswersReceived = new AtomicInteger(0);



    public ClientRequestManager(ILib lib, int[] destPorts) {
        this.lib = lib;
        this.destPorts = destPorts;
    }

    public void resetManagerForNewMessage(Transaction newMessage){
        currentTransaction = Objects.hashCode(newMessage);
        numberAnswersReceived.set(0);
    }

    public void sendMessage(Transaction message) throws Exception {
        resetManagerForNewMessage(message);
        for (int destinationPort : destPorts) {
            lib.SendAppendMessage(message, destinationPort);
        }
    }


    public void waitForResponses(){
        while(numberAnswersReceived.get() < fPlusOne) {

            /*ReentrantLock lock;
            lock.wait();
            lock.no*/
        }
    }


    public void updateOnMessageCountReceivedMessage(Transaction message){
        if(Objects.hashCode(message) == currentTransaction){
            numberAnswersReceived.incrementAndGet();
        }
    }
}
