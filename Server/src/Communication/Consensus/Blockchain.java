package Communication.Consensus;

import EVM.EVM;
import EVM.EVMClientResponse;
import EVM.Genesis.GenesisBlock;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Blockchain {
    IEVM evm;
    static final int SIZE_TRANSACTIONS_IN_BLOCK = 5;
    private final AuthenticatedPerfectLink<BaseMessage> link;
    final EVMClientResponse evmClientResponse;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type BLOCK_LIST_TYPE = new TypeToken<LinkedList<Block>>() {
    }.getType();
    LinkedList<Block> blockchain = new LinkedList<Block>();
    ArrayDeque<Transaction> transactionsToAddToBlockchain = new ArrayDeque<Transaction>();

    ReentrantLock processBlockchainLock = new ReentrantLock();
    Condition processBlockCondition = processBlockchainLock.newCondition();

    public Blockchain(AuthenticatedPerfectLink<BaseMessage> link, IEVM evm, EVMClientResponse evmClientResponse) {
        this.link = link;
        this.evm = evm;
        this.evmClientResponse = evmClientResponse;

        addInitBlock(); // ALWAYS ADD GENESIS AS INIT BLOCK
    }

    private void addInitBlock() {
        Block initBlock = getGenesisBlock();
        blockchain.add(initBlock);
    }

    private Block getGenesisBlock() {
        GenesisBlock initBlock = GenesisBlock.readGenesisBlockFromJson();
        return initBlock.toBlock();
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
        Block newBlock = new Block(SIZE_TRANSACTIONS_IN_BLOCK, prevBlockHash, blockOfTransactions);
        blockchain.add(newBlock);

        //CALL EVM TO RUN ALL THE TRANSACTIONS

        evm.processBlock(newBlock, evmClientResponse);
    }


    void addTransactionToProcessAfterConsensus(Transaction transaction) {
        transactionsToAddToBlockchain.add(transaction);
        if(transactionsToAddToBlockchain.size() >= SIZE_TRANSACTIONS_IN_BLOCK){
            processBlockCondition.signal();
        }
    }

    public void ThreadToProcessBlockchain() {
        new Thread(() -> {
            processBlockchainLock.lock();
            try {
                while (true) {
                    try {
                        processBlockCondition.await(5000L, TimeUnit.MILLISECONDS );
                        if (!transactionsToAddToBlockchain.isEmpty()) {
                            writeNewBlockToBlockChain();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            } finally {
                processBlockchainLock.unlock();
            }

        }
        ).start();
    }


    public static void writeBlocksToFile(LinkedList<Block> blocks, String filename) {
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


    public static void main(String[] args) throws IOException {
        GenesisBlock genesisBlock = GenesisBlock.readGenesisBlockFromJson();
        int genesisHash = Objects.hashCode(genesisBlock);
        System.out.println("genesis hash = " + genesisBlock);

        var x = readBlocksFromFile("Blockchain.json");

        System.out.println("blockREad = " + x.get(0));

        LinkedList<Block> blockchain = new LinkedList<Block>();

        EVM evm = new EVM();

        Transaction fakeMsg = new Transaction("fakeContract", "fakeAccount", new String[]{"fake", "val"}, "100", "fakeSignature");

        Transaction[] blockOfTransactions = new Transaction[SIZE_TRANSACTIONS_IN_BLOCK];
        for (int i = 0; i < SIZE_TRANSACTIONS_IN_BLOCK; i++) {
            blockOfTransactions[i] = fakeMsg;
        }

        int prevHah = 1234561;
        Block block1 = new Block(SIZE_TRANSACTIONS_IN_BLOCK, prevHah, blockOfTransactions);
        blockchain.add(block1);

        System.out.println("writing BlockChain");
        writeBlocksToFile(blockchain, "Blockchain.json");

        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        Block bLock2 = new Block(SIZE_TRANSACTIONS_IN_BLOCK, Objects.hashCode(block1), blockOfTransactions);
        blockchain.add(bLock2);

        writeBlocksToFile(blockchain, "Blockchain.json");

    }


}
