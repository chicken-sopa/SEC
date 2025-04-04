package EVM.Genesis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sec.BlockChain.Block;
import com.sec.BlockChain.Transaction;
import com.sec.Helpers.Constants;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class GenesisBlock {
    private String blockHash;
    private String previousBlockHash;
    private Transaction[] transactions;
    private Map<String, AccountState> state;

    private GenesisBlock() {
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }


    public Transaction[] getTransactions() {
        return transactions;
    }

    public Map<String, AccountState> getState() {
        return state;
    }

    public Block toBlock() {
        return new Block(5, Integer.getInteger(getBlockHash()), getTransactions());
    }



    public static GenesisBlock readGenesisBlockFromJson() {
        try (Reader reader = new FileReader(Constants.genesisLocation)) {

            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type genesisType = new TypeToken<GenesisBlock>() {}.getType();
            return gson.fromJson(reader, genesisType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
