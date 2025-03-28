package Communication.Consensus;

import EVM.IEVM;
import com.sec.BlockChain.Block;
import com.sec.BlockChain.Transaction;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.BaseMessage;

import com.sec.Messages.ConsensusFinishedMessage;

import java.util.*;

public class Blockchain {

    IEVM evm;
    final int SIZE_TRANSACTIONS_IN_BLOCK = 5;
    private final AuthenticatedPerfectLink<BaseMessage> link;

    LinkedList<Block> blockchain = new LinkedList<Block>();
    ArrayDeque<Transaction> transactionsToAddToBlockchain = new ArrayDeque<Transaction>();

    public Blockchain(AuthenticatedPerfectLink<BaseMessage> link, IEVM evm) {
        this.link = link;
        this.evm = evm;
    }

    void sendConsensusDoneToClient(int currentServerID, int consensusID, Transaction val, int clientID) throws Exception {
        ConsensusFinishedMessage msg = new ConsensusFinishedMessage(currentServerID, consensusID, val);
        System.out.println("------SENDING DONE TO CLIENT---------------- " + clientID + " on port" + 5550 + clientID);
        link.sendMessage(msg, 5550 + clientID);
    }

    void writeNewBlockToBlockChain() {
        // get N transactions to add to new block
        Transaction[] blockOfTransactions = new Transaction[SIZE_TRANSACTIONS_IN_BLOCK];
        for (int i = 0; i < SIZE_TRANSACTIONS_IN_BLOCK; i++) {
            blockOfTransactions[i] = transactionsToAddToBlockchain.poll();
        }
        int prevBlockHash = Objects.hashCode(blockchain.peek());
        Block newBlock = new Block(SIZE_TRANSACTIONS_IN_BLOCK, prevBlockHash,blockOfTransactions);
        blockchain.add(newBlock);

        //CALL EVM TO RUN ALL THE TRANSACTIONS
    }

    void addTransactionToProcessAfterConsensus(Transaction transaction) {
        transactionsToAddToBlockchain.add(transaction);
    }

    void ThreadToProcessBlockchain() {
        new Thread(() -> {

            while (true) {
                try {
                    wait(5000);
                    if (!transactionsToAddToBlockchain.isEmpty()) {
                         writeNewBlockToBlockChain();
                    }


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }
        ).start();
    }
}
