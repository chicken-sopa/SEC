package EVM.Genesis;

import com.google.gson.Gson;
import com.sec.BlockChain.Transaction;

import java.util.Map;

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
        Gson mapper =  new Gson();
        String json = ""; // TODO -> fetch actual Json
        try {
            return mapper.fromJson(json, GenesisBlock.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problema reading the genesis block, null returned");
            return null;
        }
    }
}
