package Communication.Consensus;

import Communication.Collection.*;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.ValTSPair.ValTSPair;
import Communication.Types.Writeset.SignedWriteset;
import Keys.KeyManager;

import java.util.*;

public class ConsensusBFT {
        //TODO PERVEBER QUANDO TEMOS CURRENT_VAL_TS
    private final ValTSPair latestWriteMsg = null;
    private final SignedWriteset writeSet = new SignedWriteset(this.SENDER_ID, KeyManager.getPrivateKey());

    private final HashMap<SignedValTSPair, Integer> writeRequestsReceived = new HashMap<>();

    private final HashMap<SignedValTSPair, Integer> acceptRequestsReceived = new HashMap<>();

    private final List<SignedValTSPair> valuesReadyToWrite = new ArrayList<>();

    private SignedValTSPair currentValTSPair = null;

    private final int currentTS = 0;

    private final int SENDER_ID = 0;

    private final int f = 1;
    private final int quorumSize;
    private final AuthenticatedPerfectLink<BaseMessage> link;

    //private


    // last message
    // messages written


    public ConsensusBFT(ValTSPair latestWriteMsg, int quorumSize, AuthenticatedPerfectLink<BaseMessage> link) throws Exception {
        this.quorumSize = quorumSize;
        this.link = link;

    }


    public Map<Integer, StateMessage> sendReadRequestAndReceiveStates(int timestampMsg) throws Exception {
        //check if proposed has clientId and sign corrected
        ConditionalCollect<BaseMessage> conditionalCollect = new ConditionalCollect<BaseMessage>(link, quorumSize);
        conditionalCollect.startCollection(timestampMsg);
        conditionalCollect.receiveMessages();
        Map<Integer, StateMessage> collectedMsg = (Map<Integer, StateMessage>) conditionalCollect.getCollectedMessages();


        return collectedMsg;
    }


    public void processReadMessage() throws Exception {
        StateMessage stateMessage = new StateMessage(this.SENDER_ID, currentValTSPair, writeSet);
        link.sendMessage(stateMessage, 4550);
    }


    public void sendCollectedMsg(Map<Integer, StateMessage> collectedStates) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        CollectedMessage collectedMessage = new CollectedMessage(this.SENDER_ID, collectedStates);
        broadcastMessage.sendBroadcast(currentTS, collectedMessage);
    }


    public ValTSPair processCollectedStatesMessage(CollectedMessage collectedMessage) {
        //TODO check if signature is valid

        //1º value of the most recent ts in bizantine quorum and if value is in writeset f+1
        //3º if none then value of the leader
        Map<Integer, StateMessage> collectedStates = collectedMessage.getCollectedStates();

        List<SignedValTSPair> collectedCurrentValues = collectedStates.values()
                .stream()
                .map(StateMessage::getVal)// Extract ValTSPair from each StateMessage
                .filter(signValTSPair -> {
                    try {
                        return signValTSPair.verifySignature(KeyManager.getClientPublicKey(signValTSPair.getClientId()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();


        int maxTsValue = collectedCurrentValues.stream()
                .max(Comparator.comparing(ValTSPair::valTS)).get().valTS();


        collectedCurrentValues = collectedCurrentValues.stream().filter((pair) ->
                pair.valTS() == maxTsValue).toList();// != maxTsValue.get().valTS());

        List<SignedWriteset> collectedWriteSets = collectedStates.values()
                .stream()
                .map(StateMessage::getWriteset)
                .filter(set -> {
                    try {
                        return set.verifyWriteset(collectedMessage.getSenderId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })// Extract ValTSPair from each StateMessage
                .toList();
        ;

        for (ValTSPair pair : collectedCurrentValues) {
            long numberTimesPairInWriteSet = collectedWriteSets.stream().map(SignedWriteset::getWriteset)
                    .flatMap(List::stream) // Flatten nested lists into a single stream
                    .filter(val -> val.getValTSPair().equals(pair)) // Count occurrences of firstValue
                    .count();
            if (numberTimesPairInWriteSet > (quorumSize + 1)) {
                return pair;
            }
        }


        if (collectedStates.get(0).getVal().getValTSPair().val() != null) {
            return collectedStates.get(0).getVal().getValTSPair();
        }

        return latestWriteMsg; // TODO ver bem porque pode estar errado mandar um write com currentValue pode ser melhor mandar null
    }


    public void sendWriteRequest(SignedValTSPair pairToWrite) throws Exception {
        //currentValTSPair = pairToWrite;
        writeSet.appendToWriteset(pairToWrite);

        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        WriteMessage writeMessage = new WriteMessage(this.SENDER_ID, pairToWrite);
        broadcastMessage.sendBroadcast(currentTS, writeMessage);
    }


    public void processWriteRequest(WriteMessage writeMessage) throws Exception {
        SignedValTSPair pairToWrite = writeMessage.getPairToProposeWrite();

        writeRequestsReceived.merge(pairToWrite, 1, Integer::sum); // update number of time write request was received

        if (writeRequestsReceived.get(pairToWrite) > (2 * f + 1)) {

            SignedValTSPair valueToAccept = pairToWrite;
            writeRequestsReceived.put(valueToAccept, -1);
            sendAccepts(valueToAccept);
        }


    }


    public void sendAccepts(SignedValTSPair pairToAccept) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        AcceptMessage acceptMessage = new AcceptMessage(this.SENDER_ID, pairToAccept);
        broadcastMessage.sendBroadcast(currentTS, acceptMessage);
    }


    public void processAcceptMessage(AcceptMessage acceptMessage) {
        SignedValTSPair pairToAccept = acceptMessage.getPairToProposeAccept();

        acceptRequestsReceived.merge(pairToAccept, 1, Integer::sum); // update number of time write request was received

        if (acceptRequestsReceived.get(pairToAccept) > (2 * f + 1)) {

            SignedValTSPair valueReadyToWrite = pairToAccept;
            acceptRequestsReceived.remove(valueReadyToWrite);
            valuesReadyToWrite.add(valueReadyToWrite);
        }

    }


    public void leaderConsensus() throws Exception {
        //readMessages = sendReadRequest();
        //process message read
        //TODO leader decides message to send



        Map<Integer, StateMessage> collectedStates = sendReadRequestAndReceiveStates(currentTS);
        sendCollectedMsg(collectedStates);

        //sendCollectedAnswers();

        //update ts
    }

    public void processConsensusRequestMessage(BaseMessage message) throws Exception {

        //when message type
        switch (message) {
            case InitCollectMessage readMsg -> processReadMessage();

            case CollectedMessage collectedMessage -> {
                SignedValTSPair pairToProposeWrite = processCollectedStatesMessage(collectedMessage);
                sendWriteRequest(pairToProposeWrite);
            }


            case WriteMessage writeMessage -> processWriteRequest(writeMessage);

            case AcceptMessage acceptMessage -> processAcceptMessage(acceptMessage);




            default:
                throw new IllegalStateException("Unexpected value: " + message);
        }
    }


}
