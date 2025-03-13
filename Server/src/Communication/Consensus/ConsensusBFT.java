package Communication.Consensus;

import Communication.Collection.*;
import  com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.AcceptMessage;
import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.ValTSPair.ValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;
import com.sec.Keys.KeyManager;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import  com.sec.Messages.BaseMessage;
import  com.sec.Messages.StateMessage;
import  com.sec.Messages.WriteMessage;


import static Configuration.ProcessConfig.getProcessId;

public class ConsensusBFT {
    //TODO PERVEBER QUANDO TEMOS CURRENT_VAL_TS
    private final ValTSPair latestWriteMsg = null;

    private ConcurrentHashMap<Integer, SignedWriteset> writesetByConsensusID = new ConcurrentHashMap<>();

    //private final SignedWriteset writeSet = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey());

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<SignedValTSPair, Integer>> writeRequestsReceivedByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<SignedValTSPair, Integer>> acceptRequestsReceivedByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, SignedValTSPair> valuesReadyToWriteByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ConsensusState> leaderConsensusState = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, SignedValTSPair> currentValTSPairByConsensusID = new ConcurrentHashMap<>();

    //private final int currentTS = 0;

    private AtomicInteger currentConsensusID = new AtomicInteger(0);

    private final Deque<SignedValTSPair> messagesFromClient = new ArrayDeque<>();

    private final int SERVER_ID;

    private final int f = 1;
    private final int quorumSize;
    private final AuthenticatedPerfectLink<BaseMessage> link;

    private final Blockchain blockchain;

    //private


    // last message
    // messages written

    public boolean isServerLeader() {
        return SERVER_ID == 0;
    }


    public ConsensusBFT(int quorumSize, AuthenticatedPerfectLink<BaseMessage> link, int serverID, Blockchain blockchain) throws Exception {
        this.quorumSize = quorumSize;
        this.link = link;
        this.SERVER_ID = serverID;
        this.blockchain = blockchain;
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
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey(getProcessId()));
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


    public SignedValTSPair processCollectedStatesMessage(CollectedMessage collectedMessage, int senderID) {
        //1º value of the most recent ts in bizantine quorum and if value is in writeset f+1
        //3º if none then value of the leader
        Map<Integer, StateMessage> collectedStates = collectedMessage.getCollectedStates();

        PublicKey nodePublicKey = KeyManager.getNodePublicKey(senderID);

        //if(collectedMessage.)


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
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey(getProcessId()));
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

        if (!pairToWrite.verifySignature(KeyManager.getClientPublicKey(pairToWrite.getClientId()))) {
            return;
        }

        ConcurrentHashMap<SignedValTSPair, Integer> writeRequestsReceived =
                writeRequestsReceivedByConsensusID.computeIfAbsent(msgConsensusID, k -> new ConcurrentHashMap<>());


        writeRequestsReceived.merge(pairToWrite, 1, Integer::sum); // update number of time write request was received

        if (writeRequestsReceived.get(pairToWrite) > (2 * f + 1)) {
            SignedValTSPair valueToAccept = pairToWrite;
            writeRequestsReceived.put(valueToAccept, -1);
            sendAccepts(valueToAccept, msgConsensusID);


        }


        long sizeOfPossibleConflictingValues = writeRequestsReceived.entrySet().stream().filter(pair -> {
            return pair.getValue() > f;
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


    public void processAcceptMessage(AcceptMessage acceptMessage) throws Exception {
        SignedValTSPair pairToAccept = acceptMessage.getPairToProposeAccept();

        if (!pairToAccept.verifySignature(KeyManager.getClientPublicKey(pairToAccept.getClientId()))) {
            return;
        }

        ConcurrentHashMap<SignedValTSPair, Integer> acceptRequestsReceived =
                acceptRequestsReceivedByConsensusID.computeIfAbsent(acceptMessage.getMsgConsensusID(), k -> new ConcurrentHashMap<>());

        acceptRequestsReceived.merge(pairToAccept, 1, Integer::sum); // update number of time write request was received

        if (acceptRequestsReceived.get(pairToAccept) > (2 * f + 1)) {

            SignedValTSPair valueReadyToWrite = pairToAccept;
            acceptRequestsReceived.remove(valueReadyToWrite);

            blockchain.writeToBlockchain(acceptMessage.getMsgConsensusID(), valueReadyToWrite.getValTSPair().val());

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

    //TODO NAME TO THINK ABOUT
    public synchronized void leaderConsensusThread() throws Exception {

        while (true) {

            startConsensus(currentConsensusID.get());

            while (leaderConsensusState.getOrDefault(currentConsensusID, ConsensusState.PROCESSING) == ConsensusState.PROCESSING) {
                try {
                    wait();  // Release lock and wait
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (leaderConsensusState.get(currentConsensusID) == ConsensusState.Decided) {
                currentConsensusID.getAndIncrement();
            } else if (leaderConsensusState.get(currentConsensusID) == ConsensusState.Aborted) {
                //currentTimestamp += 1;
                leaderConsensusState.put(currentConsensusID.getAndIncrement(), ConsensusState.PROCESSING);
            }


        }

    }


    public void startConsensus(int consensusID) throws Exception {
        //readMessages = sendReadRequest();
        //process message read
        //TODO leader decides message to send

        //int currentConsensusID = writesetByConsensusID.size();

        /*
         *  If Leader has no prior current write value or write then get one of the messages sent from the client
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
    }

    public void processConsensusRequestMessage(BaseMessage message) throws Exception {

        switch (message.getMessageType()) {
            case INIT_COLLECT -> {//InitCollectMessage initCollectMessage -> {
                processReadMessage(message.getMsgConsensusID());
            }

            case COLLECTED -> { // CollectedMessage collectedMessage -> {
                SignedValTSPair pairToProposeWrite = processCollectedStatesMessage((CollectedMessage) message, message.getSenderId());
                sendWriteRequest(pairToProposeWrite, message.getMsgConsensusID());
            }

            case WRITE -> { //WriteMessage writeMessage ->
                processWriteRequestAndSendAccept((WriteMessage) message, message.getMsgConsensusID());
            }

            case ACCEPT -> { //AcceptMessage acceptMessage ->
                 processAcceptMessage((AcceptMessage) message);}

            default -> throw new IllegalStateException("Unexpected message type: " + message.getClass().getName());
            }
        }


    }

