package Communication.Consensus;

import Communication.Collection.*;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.AcceptMessage;
import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.ValTSPair.ValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;
import com.sec.Keys.KeyManager;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sec.Messages.BaseMessage;
import com.sec.Messages.StateMessage;
import com.sec.Messages.WriteMessage;


import static Configuration.ProcessConfig.getProcessId;

public class ConsensusBFT {
    //TODO PERVEBER QUANDO TEMOS CURRENT_VAL_TS
    private final ConcurrentHashMap<Integer, SignedWriteset> writesetByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ConditionalCollect<BaseMessage>> conditionalCollectByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> writeRequestsReceivedByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> acceptRequestsReceivedByConsensusID = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ConsensusState> leaderConsensusState = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, SignedValTSPair> currentValTSPairByConsensusID = new ConcurrentHashMap<>();

    //private final int currentTS = 0;

    private final AtomicInteger currentConsensusID = new AtomicInteger(0);

    public final Deque<SignedValTSPair> messagesFromClient = new ArrayDeque<>();

    private final int SERVER_ID;

    private final int f = 1;
    private final int quorumSize;
    private final AuthenticatedPerfectLink<BaseMessage> link;

    private final Blockchain blockchain;


    private final Lock leaderConsensusThreadLock = new ReentrantLock();
    private final Condition conditionConsensus = leaderConsensusThreadLock.newCondition();

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

        // we save the conditional collect object to be able to update msg received when receiving msg state
        conditionalCollectByConsensusID.put(currentConsensusID, conditionalCollect);

        conditionalCollect.startCollection(currentConsensusID);
        conditionalCollect.waitForStateMessages();


        return (Map<Integer, StateMessage>) conditionalCollect.getCollectedMessages();
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
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<>(link, quorumSize);
        CollectedMessage collectedMessage = new CollectedMessage(this.SERVER_ID, collectedStates, msgConsensusID);
        broadcastMessage.sendBroadcast(collectedMessage);
    }


    public SignedValTSPair processCollectedStatesMessage(CollectedMessage collectedMessage, int senderID) {
        //1º value of the most recent ts in byzantine quorum and if value is in writeset f+1
        //3º if none then value of the leader
        Map<Integer, StateMessage> collectedStates = collectedMessage.getCollectedStates();

        PublicKey nodePublicKey = KeyManager.getPublicKey(senderID);

        List<SignedValTSPair> collectedCurrentValues = collectedStates.values()
                .stream()
                .map(StateMessage::getVal)// Extract ValTSPair from each StateMessage
                .filter(signValTSPair -> {
                    try {
                        return signValTSPair.verifySignature(KeyManager.getPublicKey(signValTSPair.getClientId()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();


        Optional<SignedValTSPair> possibleMaxTsValue = collectedCurrentValues.stream()
                .max(Comparator.comparing((pair) -> pair.getValTSPair().valTS()));

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


            for (SignedValTSPair pair : collectedCurrentValues) {
                long numberTimesPairInWriteSet = collectedWriteSets.stream().map(SignedWriteset::getWriteset)
                        .flatMap(List::stream) // Flatten nested lists into a single stream
                        .filter(val -> val.equals(pair)) // Count occurrences of firstValue
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


        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<>(link, quorumSize);
        WriteMessage writeMessage = new WriteMessage(this.SERVER_ID, pairToWrite, msgConsensusID);
        broadcastMessage.sendBroadcast(writeMessage);
    }


    public void processWriteRequestAndSendAccept(WriteMessage writeMessage, int msgConsensusID) throws Exception {
        //TODO CECK IF -1! THEN DONT SEND MORE

        SignedValTSPair pairToWrite = writeMessage.getPairToProposeWrite();

        if (!pairToWrite.verifySignature(KeyManager.getPublicKey(pairToWrite.getClientId()))) {
            return;
        }

        ValTSPair valTSPair = pairToWrite.getValTSPair();

        ConcurrentHashMap<Integer, Integer> writeRequestsReceived =
                writeRequestsReceivedByConsensusID.computeIfAbsent(msgConsensusID, k -> new ConcurrentHashMap<>());

        writeRequestsReceived.merge(pairToWrite.hashCode(), 1, Integer::sum); // update number of time write request was received
        System.out.println("received " + writeRequestsReceived.get(pairToWrite.hashCode()));
        if (writeRequestsReceived.get(pairToWrite.hashCode()) >= (2 * f + 1)) {
            SignedValTSPair valueToAccept;
            valueToAccept = pairToWrite;
            writeRequestsReceived.put(valueToAccept.hashCode(), -1);
            sendAccepts(valueToAccept, msgConsensusID);


        }


        long sizeOfPossibleConflictingValues = writeRequestsReceived.entrySet().stream().filter(pair -> pair.getValue() > f).count(); // if more than 1 has votes bigger that f then abort because none will have 2f+1

        if (isServerLeader() && sizeOfPossibleConflictingValues > 1) {
            leaderConsensusState.put(msgConsensusID, ConsensusState.Aborted);
            wakeUpConsensusLeader();
        }


    }


    public void sendAccepts(SignedValTSPair pairToAccept, int msgConsensusID) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<>(link, quorumSize);
        AcceptMessage acceptMessage = new AcceptMessage(this.SERVER_ID, pairToAccept, msgConsensusID);
        broadcastMessage.sendBroadcast(acceptMessage);
    }


    public void processAcceptMessage(AcceptMessage acceptMessage) throws Exception {
        SignedValTSPair pairToAccept = acceptMessage.getPairToProposeAccept();

        if (!pairToAccept.verifySignature(KeyManager.getPublicKey(pairToAccept.getClientId()))) {
            return;
        }

        ConcurrentHashMap<Integer, Integer> acceptRequestsReceived =
                acceptRequestsReceivedByConsensusID.computeIfAbsent(acceptMessage.getMsgConsensusID(), k -> new ConcurrentHashMap<>());

        acceptRequestsReceived.merge(pairToAccept.hashCode(), 1, Integer::sum); // update number of time write request was received

        if (acceptRequestsReceived.get(pairToAccept.hashCode()) >= (2 * f + 1)) {

            SignedValTSPair valueReadyToWrite;
            valueReadyToWrite = pairToAccept;
            acceptRequestsReceived.remove(valueReadyToWrite.hashCode());

            blockchain.writeToBlockchain(acceptMessage.getMsgConsensusID(), valueReadyToWrite.getValTSPair().val());

            if (isServerLeader()) {
                leaderConsensusState.put(acceptMessage.getMsgConsensusID(), ConsensusState.Decided);
                wakeUpConsensusLeader();
            }


        }

        long sizeOfPossibleConflictingValues = acceptRequestsReceived.entrySet().stream().filter(pair -> pair.getValue() < f).count(); // if more than 1 has votes bigger that f then abort because none will have 2f+1

        if (isServerLeader() && sizeOfPossibleConflictingValues > 1) {
            leaderConsensusState.put(acceptMessage.getMsgConsensusID(), ConsensusState.Aborted);
            wakeUpConsensusLeader();
        }

    }

    //TODO NAME TO THINK ABOUT
    public synchronized void leaderConsensusThread() throws Exception {

        while (!messagesFromClient.isEmpty()) {

            startConsensus(currentConsensusID.get());

            while (leaderConsensusState.getOrDefault(currentConsensusID.get(), ConsensusState.PROCESSING) == ConsensusState.PROCESSING) {
                try {
                    leaderConsensusThreadLock.lock();
                    try {
                        conditionConsensus.await(); // Equivalent to wait()
                    } finally {
                        leaderConsensusThreadLock.unlock();
                    }  // Release lock and wait
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (leaderConsensusState.get(currentConsensusID.get()) == ConsensusState.Decided) {
                currentConsensusID.getAndIncrement();
                System.out.println("Consensus is decided");
            } else if (leaderConsensusState.get(currentConsensusID.get()) == ConsensusState.Aborted) {
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

        System.out.println("-------------------------------------------");
        System.out.println("Start Consenusus with ID = " + consensusID);

        if (writesetByConsensusID.get(consensusID) == null && currentValTSPairByConsensusID.get(consensusID) == null) {

            //SignedValTSPair newPair = new SignedValTSPair();
            currentValTSPairByConsensusID.put(consensusID, messagesFromClient.pollFirst());

            System.out.println("Value to propose chosen from client = " + currentValTSPairByConsensusID);
        }


        Map<Integer, StateMessage> collectedStates = sendReadRequestAndReceiveStates(consensusID);

        System.out.println("Received collected Messages");
        System.out.println("Sending Collected Messages for Consensus = " + consensusID);
        sendCollectedMsg(collectedStates, consensusID);
    }

    public void processConsensusRequestMessage(BaseMessage message) throws Exception {

        switch (message.getMessageType()) {
            case INIT_COLLECT -> {
                processReadMessage(message.getMsgConsensusID());
            }

            case STATE -> {
                ConditionalCollect<BaseMessage> conditionalCollect = conditionalCollectByConsensusID.get(message.getMsgConsensusID());
                if (conditionalCollect != null) {
                    conditionalCollect.processStateMessage(message);
                }
            }

            case COLLECTED -> { // CollectedMessage collectedMessage -> {
                SignedValTSPair pairToProposeWrite = processCollectedStatesMessage((CollectedMessage) message, message.getSenderId());
                sendWriteRequest(pairToProposeWrite, message.getMsgConsensusID());
            }

            case WRITE -> {
                processWriteRequestAndSendAccept((WriteMessage) message, message.getMsgConsensusID());
            }

            case ACCEPT -> {
                processAcceptMessage((AcceptMessage) message);
            }

            default -> throw new IllegalStateException("Unexpected message type: " + message.getClass().getName());
        }
    }

    private void wakeUpConsensusLeader() {
        leaderConsensusThreadLock.lock();
        try {
            conditionConsensus.signal(); // Wake up one waiting thread
        } finally {
            leaderConsensusThreadLock.unlock();
        }
    }

}

