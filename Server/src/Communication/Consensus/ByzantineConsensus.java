package Communication.Consensus;

import com.sec.Messages.CollectedMessage;
import Communication.Collection.ConditionalCollect;
import Communication.Collection.ReadMessage;
import com.sec.BlockChain.Transaction;
import com.sec.Keys.KeyManager;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;

import static Configuration.ProcessConfig.getProcessId;

public class ByzantineConsensus extends ConsensusBFT {
    DigitalSignatureAuth<BaseMessage> digitalSignatureAuth;

    int consensusByzantineActionID;

    public ByzantineConsensus(int quorumSize, AuthenticatedPerfectLink<BaseMessage> link, int serverID, Blockchain blockchain, DigitalSignatureAuth<BaseMessage> digitalSignatureAuth, int typeByzantineAction) throws Exception {
        super(quorumSize, link, serverID, blockchain);
        consensusByzantineActionID = typeByzantineAction;
        this.digitalSignatureAuth = digitalSignatureAuth;

        System.out.println("---------------------THIS NODE IS BYZANTINE!!!!!!---------------------");

        tryStartFakeByzantineConsensus();
    }



    public void tryStartFakeByzantineConsensus() throws Exception {
        if (consensusByzantineActionID == 0) {
            int currentConsensusID = 0;

            //check if proposed has clientId and sign corrected
            ConditionalCollect<BaseMessage> conditionalCollect = new ConditionalCollect<BaseMessage>(link, quorumSize);

            // we save the conditional collect object to be able to update msg received when receiving msg state
            conditionalCollectByConsensusID.put(currentConsensusID, conditionalCollect);

            conditionalCollect.startCollection(currentConsensusID);

            //WAIT 5 SEC TO GET MESSAGES
            wait(5000);

            if (conditionalCollect.getCollectedMessages().size() >= this.quorumSize) {
                System.out.println("BYZANTINE PROCESS STARTED CONSENSUS WHEN NOT LEADER");
            }else {
                System.out.println("BYZANTINE PROCESS COULDN'T START CONSENSUS WHEN NOT LEADER");
            }
        }


    }


    /**
     * Create Fake Value in response from Read Messages
     * This overrides the process Read Message
     */
    @Override
    public void processReadMessage(ReadMessage msg) throws Exception {
        if (consensusByzantineActionID == 1) {
        SignedValTSPair byzantineVal = createByzantineVal();

        int msgConsensusID = msg.getMsgConsensusID();

        this.currentValTSPairByConsensusID.put(msg.getMsgConsensusID(), byzantineVal);

        SignedWriteset currentWriteset = writesetByConsensusID.get(msgConsensusID);

        if (currentWriteset == null) {
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey(getProcessId()));
            currentWriteset.appendToWriteset(byzantineVal); // append fake value
            writesetByConsensusID.put(msgConsensusID, currentWriteset);
        }

        BaseMessage stateMessage = createStateMessage(msg.getMsgConsensusID()).toBaseMessage(); // SEND IT HAS BASE MSG AND TRY MANUAL TRANSFORM IN RECEIVER
        System.out.println("SENDING STATUS RESPONSE WITH == " + stateMessage.prettyPrint());
        link.sendMessage(stateMessage, 4550);
        ;}
        else{
            super.processReadMessage(msg);
        }

    }

    public SignedValTSPair createByzantineVal() throws Exception {
        Transaction fakeMsg = new Transaction("fakeContract", "fakeAccount", new String[]{"fake", "val"}, "100", 100);
        String fakeSignature = digitalSignatureAuth.signMessageVal(String.valueOf(fakeMsg), KeyManager.getPrivateKey(this.SERVER_ID));
        return new SignedValTSPair(0, fakeMsg, this.CLIENT_ID, fakeSignature);
    }

    /**
     * Check if byzantine value got append in the collected values
     */

    public SignedValTSPair processCollectedStatesMessage(CollectedMessage collectedMessage, int senderID) {
        if (consensusByzantineActionID == 1) {
            try {
                SignedValTSPair byzantineVal = createByzantineVal();
                if (collectedMessage.getCollectedStates().get(this.SERVER_ID).getWriteset().getWriteset().contains(byzantineVal)) {
                    System.out.println("BYZANTINE STATE MSG WAS ACCEPTED BY LEADER");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return super.processCollectedStatesMessage(collectedMessage, senderID);
    }


    @Override
    public void sendWriteRequest(SignedValTSPair pairToWrite, int msgConsensusID) throws Exception {
        if (consensusByzantineActionID == 3) {
            SignedValTSPair byzantineVal = createByzantineVal();
            super.sendWriteRequest(byzantineVal, msgConsensusID);

            wait(5000);
            if(this.writeRequestsReceivedByConsensusID.get(msgConsensusID).get(pairToWrite.hashCode()) >= 1){
                System.out.println("BYZANTINE WRITE MSG WAS ACCEPTED");
            }else {
                System.out.println("BYZANTINE WRITE MSG WAS NOT ACCEPTED");
            }

        } else {
            super.sendWriteRequest(pairToWrite, msgConsensusID);
        }
    }
}
