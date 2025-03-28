package EVM.Genesis;

import com.google.gson.Gson;
import com.sec.BlockChain.Transaction;
import com.sec.Helpers.Constants;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class GenesisBlock {
    public String blockHash;
    public String previousBlockHash;
    public String blockChainOwnerAddress;
    public Transaction[] transactions;
    public Map<String, AccountState> state;

    private GenesisBlock() {}

    public String getBlockHash() {
        return blockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getBlockChainOwnerAddress() {
        return blockChainOwnerAddress;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public Map<String, AccountState> getState() {
        return state;
    }

    public static GenesisBlock readGenesisBlockFromJson(){
        String json = FetchJson();
        Gson mapper =  new Gson();
        try {
            return mapper.fromJson(json, GenesisBlock.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Genesis block written in incorrect format, null returned");
            return null;
        }
    }

    private static String FetchJson(){

        InputStream inputStream = GenesisBlock.class
                .getClassLoader()
                .getResourceAsStream(Constants.genesisLocation);
        if (inputStream == null) {
            throw new RuntimeException("File not found: Genesis.json");
        }

        return new Scanner(inputStream, StandardCharsets.UTF_8)
                .useDelimiter("\\A")
                .next();
    }
}
