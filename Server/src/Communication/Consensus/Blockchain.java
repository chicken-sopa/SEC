package Communication.Consensus;

import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.BaseMessage;

import com.sec.Messages.ConsensusFinishedMessage;

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
        ConsensusFinishedMessage msg = new ConsensusFinishedMessage(currentServerID, consensusID, val);
        System.out.println("------SENDING DONE TO CLIENT---------------- " +  clientID + " on port" + 5550 + clientID);
        link.sendMessage(msg, 5550 + clientID);
     }

}
