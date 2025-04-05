package com.sec.BlockChain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sec.Helpers.Constants;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class Block implements Serializable {
    private Integer blockHash;
    private Integer previousBlockHash;
    private Transaction[] transactions;
    private Map<String, AccountState> state;

    public Block(int blockSize, Integer prevBlockHash, Transaction... transactions) {
        this.transactions = new Transaction[blockSize];
        System.arraycopy(transactions, 0, this.transactions, 0, transactions.length);
        this.previousBlockHash = prevBlockHash;

        this.blockHash = Objects.hashCode(this);

    }

    public Integer getBlockHash() {
        return blockHash;
    }

    public Integer getPreviousBlockHash() {
        return previousBlockHash;
    }


    public Transaction[] getTransactions() {
        return transactions;
    }

    public Map<String, AccountState> getState() {
        return state;
    }

    public Block toBlock() {
        return new Block(5, getBlockHash(), getTransactions());
    }

    public static Block readGenesisBlockFromJson() {
        try (Reader reader = new FileReader(Constants.genesisLocation)) {

            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type genesisType = new TypeToken<Block>() {}.getType();
            return gson.fromJson(reader, genesisType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
