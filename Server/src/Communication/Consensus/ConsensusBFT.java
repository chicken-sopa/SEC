package Communication.Consensus;

import Communication.Types.ValTSPair.ValTSPair;

import java.util.List;

public class ConsensusBFT {

    ValTSPair latestWriteMsg;
    List<ValTSPair> writeSet;

    // last message
    // messages written



    public void sendReadRequest(){
        //ConditionalCollect<> conditionalCollect = new ConditionalCollect();
        //conditionalCollect.startCollection();
        //conditionalCollect.receiveMessages();
        //return conditionalCollect.collectedMessages();


    }

    public void sendStateAnswer(){

    }

    public void sendCollectedAnswers(){

    }


    public void sendWriteRequest(){

    }


    public void sendAccepts(){

    }


    public void leaderConsensus(){
        sendReadRequest();
        sendStateAnswer();
        sendCollectedAnswers();
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
