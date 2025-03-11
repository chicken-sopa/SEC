package Communication.Consensus;

import Communication.Collection.BaseMessage;
import Communication.Collection.CollectedMessage;
import Communication.Collection.ConditionalCollect;
import Communication.Collection.StateMessage;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Types.ValTSPair.ValTSPair;
import Keys.KeyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsensusBFT {

    private  ValTSPair latestWriteMsg = null;
    private  List<ValTSPair> writeSet = null;
    private int currentTS = 0;

    private final int quorumSize;
    private final AuthenticatedPerfectLink<BaseMessage> link;

    // last message
    // messages written


    public ConsensusBFT(ValTSPair latestWriteMsg, int quorumSize, AuthenticatedPerfectLink<BaseMessage> link){
        this.quorumSize = quorumSize;
        this.link = link;

    }


    public Map<Integer, StateMessage> sendReadRequest(int timestampMsg) throws Exception {
            //check if proposed has clientId and sign corrected
        ConditionalCollect<BaseMessage> conditionalCollect = new ConditionalCollect<BaseMessage>(link, quorumSize);
        conditionalCollect.startCollection(timestampMsg);
        conditionalCollect.receiveMessages();
        Map<Integer, StateMessage> collectedMsg =  (Map<Integer, StateMessage>) conditionalCollect.getCollectedMessages();


        

        return collectedMsg;
    }


    public void sendCollectedMsg(Map<Integer, StateMessage> collectedStates) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        CollectedMessage collectedMessage = new CollectedMessage(1, collectedStates);
        broadcastMessage.sendBroadcast(currentTS, collectedMessage);
    }


    public void processCollectedStatesMessage(Map<Integer, StateMessage> collectedStates){
        //TODO check if signature is valid

        //1º value of the most recent ts in bizantine quorum and if value is in writeset f+1
        //3º if none then value of the leader

        HashMap<ValTSPair, Integer> quantitiesOfProposedValues = new HashMap<ValTSPair, Integer>();



        collectedStates.forEach((senderId,state)->{
            try {
                if(state.getVal().verifySignature(KeyManager.getClientPublicKey(state.getVal().getClientId()))){
                    ValTSPair currentVal = state.getVal().getValTSPair();
                    quantitiesOfProposedValues.merge(currentVal, 1, Integer::sum);
                    // break;

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


    }



    public void sendWriteRequest(){

    }


    public void sendAccepts(){

    }


    public void leaderConsensus(){
        //readMessages = sendReadRequest();
        //process message read

        //sendStateAnswer();
        //sendCollectedAnswers();

        //update ts
    }

    public void processConsensusRequestMessage(/* msg : Consensus */){

        //when message type


        //process read request
        //process state response
        //process collected response
        //process write response
        //process accept response



    }




}
