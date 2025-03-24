package com.sec.BlockChain;

public class Block {

    private final Transaction[] transactions;

    public Block(int blockSize, Transaction... transactions) {
        this.transactions = new Transaction[blockSize];
        System.arraycopy(transactions, 0, this.transactions, 0, transactions.length);
    }

    public Transaction[] getTransactions() {
        return transactions;
    }
}
