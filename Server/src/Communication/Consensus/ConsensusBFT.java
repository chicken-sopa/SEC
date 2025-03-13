package Communication.Consensus;

import Communication.Collection.*;
import Communication.Links.AuthenticatedPerfectLink;
import Communication.Messages.AcceptMessage;
import Communication.Messages.BaseMessage;
import Communication.Messages.StateMessage;
import Communication.Messages.WriteMessage;
import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.ValTSPair.ValTSPair;
import Communication.Types.Writeset.SignedWriteset;
import Keys.KeyManager;

import java.util.*;

public class ConsensusBFT {
    //TODO PERVEBER QUANDO TEMOS CURRENT_VAL_TS
    private final ValTSPair latestWriteMsg = null;

    private HashMap<Integer, SignedWriteset> writesetByConsensusID = new HashMap<>();

    //private final SignedWriteset writeSet = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey());

    private final HashMap<Integer, HashMap<SignedValTSPair, Integer>> writeRequestsReceivedByConsensusID = new HashMap<>();

    private final HashMap<Integer, HashMap<SignedValTSPair, Integer>> acceptRequestsReceivedByConsensusID = new HashMap<>();

    private final HashMap<Integer, SignedValTSPair> valuesReadyToWrite = new HashMap<>();

    private final HashMap<Integer, ConsensusState> leaderConsensusState = new HashMap<>();

    private HashMap<Integer, SignedValTSPair> currentValTSPairByConsensusID = new HashMap<>();

    //private final int currentTS = 0;

    private Integer currentConsensusID = 0;

    private final Deque<SignedValTSPair> messagesFromClient = new ArrayDeque<>();

    private final int SERVER_ID;

    private final int f = 1;
    private final int quorumSize;
    private final AuthenticatedPerfectLink<BaseMessage> link;

    //private


    // last message
    // messages written

    public boolean isServerLeader() {
        return SERVER_ID == 0;
    }


    public ConsensusBFT(ValTSPair latestWriteMsg, int quorumSize, AuthenticatedPerfectLink<BaseMessage> link, int serverID) throws Exception {
        this.quorumSize = quorumSize;
        this.link = link;
        this.SERVER_ID = serverID;
    }


    public Map<Integer, StateMessage> sendReadRequestAndReceiveStates(int currentConsensusID) throws Exception {
        //check if proposed has clientId and sign corrected
        ConditionalCollect<BaseMessage> conditionalCollect = new ConditionalCollect<BaseMessage>(link, quorumSize);
        conditionalCollect.startCollection(currentConsensusID);
        conditionalCollect.receiveMessages();
        Map<Integer, StateMessage> collectedMsg = (Map<Integer, StateMessage>) conditionalCollect.getCollectedMessages();


        return collectedMsg;
    }


    public void processReadMessage(int msgConsensusID) throws Exception {
        SignedWriteset currentWriteset = writesetByConsensusID.get(msgConsensusID);

        if (currentWriteset == null) {
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey());
            writesetByConsensusID.put(msgConsensusID, currentWriteset);
        }

        SignedValTSPair currentValTsPair = currentValTSPairByConsensusID.get(msgConsensusID);

        StateMessage stateMessage = new StateMessage(this.SERVER_ID, currentValTsPair, currentWriteset, msgConsensusID);
        link.sendMessage(stateMessage, 4550);
    }


    public void sendCollectedMsg(Map<Integer, StateMessage> collectedStates, int msgConsensusID) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        CollectedMessage collectedMessage = new CollectedMessage(this.SERVER_ID, collectedStates, msgConsensusID);
        broadcastMessage.sendBroadcast(collectedMessage);
    }


    public SignedValTSPair processCollectedStatesMessage(CollectedMessage collectedMessage) {
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


        Optional<SignedValTSPair> possibleMaxTsValue = collectedCurrentValues.stream()
                .max(Comparator.comparing((pair) -> {
                    return pair.getValTSPair().valTS();
                }));

        if (possibleMaxTsValue.isPresent()) {

            int maxTsValue = possibleMaxTsValue.get().getValTSPair().valTS();


            collectedCurrentValues = collectedCurrentValues.stream().filter((pair) ->
                    pair.getValTSPair().valTS() == maxTsValue).toList();// != maxTsValue.get().valTS());

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

            for (SignedValTSPair pair : collectedCurrentValues) {
                long numberTimesPairInWriteSet = collectedWriteSets.stream().map(SignedWriteset::getWriteset)
                        .flatMap(List::stream) // Flatten nested lists into a single stream
                        .filter(val -> val.getValTSPair().equals(pair)) // Count occurrences of firstValue
                        .count();
                if (numberTimesPairInWriteSet > (quorumSize + 1)) {
                    return pair;
                }
            }
        }


        return collectedStates.get(0).getVal(); // when we have nothing always value that leader has


    }


    public void sendWriteRequest(SignedValTSPair pairToWrite, int msgConsensusID) throws Exception {
        //currentValTSPair = pairToWrite;

        SignedWriteset currentWriteset = writesetByConsensusID.get(msgConsensusID);

        if (currentWriteset == null) {
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey());
            writesetByConsensusID.put(msgConsensusID, currentWriteset);
        }

        // update writeSet and current Value to Write in specific consensusID
        currentWriteset.appendToWriteset(pairToWrite);
        currentValTSPairByConsensusID.put(msgConsensusID, pairToWrite);


        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        WriteMessage writeMessage = new WriteMessage(this.SERVER_ID, pairToWrite, msgConsensusID);
        broadcastMessage.sendBroadcast(writeMessage);
    }


    public void processWriteRequestAndSendAccept(WriteMessage writeMessage, int msgConsensusID) throws Exception {
        //TODO CECK IF -1! THEN DONT SEND MORE

        SignedValTSPair pairToWrite = writeMessage.getPairToProposeWrite();

        HashMap<SignedValTSPair, Integer> writeRequestsReceived =
                writeRequestsReceivedByConsensusID.computeIfAbsent(msgConsensusID, k -> new HashMap<>());


        writeRequestsReceived.merge(pairToWrite, 1, Integer::sum); // update number of time write request was received

        if (writeRequestsReceived.get(pairToWrite) > (2 * f + 1)) {
            SignedValTSPair valueToAccept = pairToWrite;
            writeRequestsReceived.put(valueToAccept, -1);
            sendAccepts(valueToAccept, msgConsensusID);


        }


        long sizeOfPossibleConflictingValues = writeRequestsReceived.entrySet().stream().filter(pair -> {
            return pair.getValue() < f;
        }).count(); // if more than 1 has votes bigger that f then abort because none will have 2f+1

        if (isServerLeader() && sizeOfPossibleConflictingValues > 1) {
            leaderConsensusState.put(msgConsensusID, ConsensusState.Aborted);
            notifyAll();
        }


    }


    public void sendAccepts(SignedValTSPair pairToAccept, int msgConsensusID) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<BaseMessage>(link, quorumSize);
        AcceptMessage acceptMessage = new AcceptMessage(this.SERVER_ID, pairToAccept, msgConsensusID);
        broadcastMessage.sendBroadcast(acceptMessage);
    }


    public void processAcceptMessage(AcceptMessage acceptMessage) {
        SignedValTSPair pairToAccept = acceptMessage.getPairToProposeAccept();

        HashMap<SignedValTSPair, Integer> acceptRequestsReceived =
                acceptRequestsReceivedByConsensusID.computeIfAbsent(acceptMessage.getMsgConsensusID(), k -> new HashMap<>());

        acceptRequestsReceived.merge(pairToAccept, 1, Integer::sum); // update number of time write request was received

        if (acceptRequestsReceived.get(pairToAccept) > (2 * f + 1)) {

            SignedValTSPair valueReadyToWrite = pairToAccept;
            acceptRequestsReceived.remove(valueReadyToWrite);
            valuesReadyToWrite.put(acceptMessage.getMsgConsensusID(), valueReadyToWrite);

            if (isServerLeader()) {
                leaderConsensusState.put(acceptMessage.getMsgConsensusID(), ConsensusState.Decided);
                notifyAll();
            }

        }

        long sizeOfPossibleConflictingValues = acceptRequestsReceived.entrySet().stream().filter(pair -> {
            return pair.getValue() < f;
        }).count(); // if more than 1 has votes bigger that f then abort because none will have 2f+1

        if (isServerLeader() && sizeOfPossibleConflictingValues > 1) {
            leaderConsensusState.put(acceptMessage.getMsgConsensusID(), ConsensusState.Aborted);
            notifyAll();
        }

    }


    public synchronized void leaderConsensusThread() throws Exception {

        while (true) {

            startConsensus(currentConsensusID);

            while (leaderConsensusState.getOrDefault(currentConsensusID, ConsensusState.PROCESSING) == ConsensusState.PROCESSING) {
                try {
                    wait();  // Release lock and wait
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (leaderConsensusState.get(currentConsensusID) == ConsensusState.Decided) {
                currentConsensusID += 1;
            } else if (leaderConsensusState.get(currentConsensusID) == ConsensusState.Aborted) {
                //currentTimestamp += 1;
                leaderConsensusState.put(currentConsensusID, ConsensusState.PROCESSING);
            }


        }

    }


    public void startConsensus(int consensusID) throws Exception {
        //readMessages = sendReadRequest();
        //process message read
        //TODO leader decides message to send

        //int currentConsensusID = writesetByConsensusID.size();

        /*
         * If Leader has no prior current write value or write then get one of the messages sent from the client
         * TODO
         *   -ask how to get messages from client
         *   -make that when no messages from client wait()
         *   -when message the notify() the thread if a sleep
         **/

        if (writesetByConsensusID.get(consensusID) == null && currentValTSPairByConsensusID.get(consensusID) == null) {
            currentValTSPairByConsensusID.put(consensusID, messagesFromClient.pollFirst());
        }


        Map<Integer, StateMessage> collectedStates = sendReadRequestAndReceiveStates(consensusID);
        sendCollectedMsg(collectedStates, consensusID);

        //sendCollectedAnswers();

        //update ts
    }

    public void processConsensusRequestMessage(BaseMessage message) throws Exception {

        if (message instanceof InitCollectMessage) {
            processReadMessage(message.getMsgConsensusID());

        } else if (message instanceof CollectedMessage collectedMessage) {// Pattern matching
            SignedValTSPair pairToProposeWrite = processCollectedStatesMessage(collectedMessage);
            sendWriteRequest(pairToProposeWrite, message.getMsgConsensusID());

        } else if (message instanceof WriteMessage writeMessage) {
            processWriteRequestAndSendAccept(writeMessage, message.getMsgConsensusID());

        } else if (message instanceof AcceptMessage acceptMessage) {
            processAcceptMessage(acceptMessage);

        } else {
            throw new IllegalStateException("Unexpected message type: " + message.getClass().getName());
        }
    }


}

