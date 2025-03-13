package Communication.Consensus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Blockchain {

    ConcurrentHashMap<Integer, String> blockchain = new ConcurrentHashMap<>();

    public Blockchain(){}

    void writeToBlockchain(Integer consensusID , String valueToAppend){
        this.blockchain.put(consensusID, valueToAppend);
    }


     List<String> readValue(){
        return  blockchain.values().stream().toList() ;
    }

}
