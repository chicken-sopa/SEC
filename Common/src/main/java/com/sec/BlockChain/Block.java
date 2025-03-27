package com.sec.BlockChain;

public class Block {

    private final Transaction[] transactions;
    private int prevBlockHash;

    public Block(int blockSize, int prevBlockHash, Transaction... transactions) {
        this.transactions = new Transaction[blockSize];
        System.arraycopy(transactions, 0, this.transactions, 0, transactions.length);
        this.prevBlockHash = prevBlockHash;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public int getPrevBlockHash() {
        return prevBlockHash;
    }
}
