package Communication.Consensus;

import EVM.EVMClientResponse;
import EVM.IEVM;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sec.BlockChain.Block;
import com.sec.BlockChain.Transaction;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.BaseMessage;

import com.sec.Messages.ConsensusFinishedMessage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Blockchain {
    IEVM evm;
    static final int SIZE_TRANSACTIONS_IN_BLOCK = 5;
    private final AuthenticatedPerfectLink<BaseMessage> link;
    final EVMClientResponse evmClientResponse;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type BLOCK_LIST_TYPE = new TypeToken<LinkedList<Block>>() {}.getType();
    LinkedList<Block> blockchain = new LinkedList<Block>();
    ArrayDeque<Transaction> transactionsToAddToBlockchain = new ArrayDeque<Transaction>();

    public Blockchain(AuthenticatedPerfectLink<BaseMessage> link, IEVM evm, EVMClientResponse evmClientResponse) {
        this.link = link;
        this.evm = evm;
        this.evmClientResponse = evmClientResponse;
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

        evm.processBlock(newBlock, evmClientResponse);
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


    public static void writeBlocksToFile(Block[] blocks, String filename) {
        try (Writer writer = new FileWriter(filename)) {
            // Convert blocks array to JSON and write to file
            gson.toJson(blocks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Block> readBlocksFromFile(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            return gson.fromJson(reader, BLOCK_LIST_TYPE);
        }
    }


    public static void main(String[] args) {
        LinkedList<Block> blockchain = new LinkedList<Block>();
        Transaction fakeMsg = new Transaction("fakeContract", "fakeAccount", new String[]{"fake", "val"}, "fakeSignature");

        Transaction[] blockOfTransactions = new Transaction[SIZE_TRANSACTIONS_IN_BLOCK];
        for (int i = 0; i < SIZE_TRANSACTIONS_IN_BLOCK; i++) {
            blockOfTransactions[i] = fakeMsg;
        }

        int prevHah = 1234561;
        Block block = new Block(SIZE_TRANSACTIONS_IN_BLOCK, prevHah, blockOfTransactions);
        blockchain.add(block);

        writeBlocksToFile(blockchain.toArray(Block[]::new), "Blockchain.json");

    }


}
