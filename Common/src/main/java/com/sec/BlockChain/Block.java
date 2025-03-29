package com.sec.BlockChain;

import java.io.Serializable;
import java.util.Objects;

public class Block implements Serializable {

    private final Transaction[] transactions;
    private Integer prevBlockHash;


    public Block(int blockSize, Integer prevBlockHash, Transaction... transactions) {
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
