package Communication.Consensus;

import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.BaseMessage;

import com.sec.Messages.ConsensusFinished;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Blockchain {

    ConcurrentHashMap<Integer, String> blockchain = new ConcurrentHashMap<>();
    private final AuthenticatedPerfectLink<BaseMessage> link;

    public Blockchain(AuthenticatedPerfectLink<BaseMessage> link){
        this.link = link;
    }

    void writeToBlockchain(Integer consensusID , String valueToAppend){
        this.blockchain.put(consensusID, valueToAppend);
    }


     List<String> readValue(){
        return  blockchain.values().stream().toList() ;
    }

     void sendConsensusDoneToClient(int currentServerID, int consensusID, String val, int clientID) throws Exception {
        ConsensusFinished msg = new ConsensusFinished(currentServerID, consensusID, val);
        link.sendMessage(msg, clientID);
     }

}
