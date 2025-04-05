package Communication.Consensus;

import Communication.Collection.*;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.*;
import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Messages.Types.Writeset.SignedWriteset;
import com.sec.Keys.KeyManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import static Configuration.ProcessConfig.getProcessId;



public class ConsensusBFT {
    final int LEADER_ID = 0;
    final int CLIENT_ID = 0;
     final ConcurrentHashMap<Integer, SignedWriteset> writesetByConsensusID = new ConcurrentHashMap<>();

     final ConcurrentHashMap<Integer, ConditionalCollect<BaseMessage>> conditionalCollectByConsensusID = new ConcurrentHashMap<>();

     final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> writeRequestsReceivedByConsensusID = new ConcurrentHashMap<>();

     final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> acceptRequestsReceivedByConsensusID = new ConcurrentHashMap<>();

     final ConcurrentHashMap<Integer, ConsensusState> leaderConsensusState = new ConcurrentHashMap<>();

     final ConcurrentHashMap<Integer, SignedValTSPair> currentValTSPairByConsensusID = new ConcurrentHashMap<>();

     //final int currentTS = 0;

     final AtomicInteger currentConsensusID = new AtomicInteger(0);

     public final Deque<SignedValTSPair> messagesFromClient = new ArrayDeque<>();

     final int SERVER_ID;

     final int f = 1; // TODO THIS IS WRONG // F NEEDS TO BE CALCULATED

    final int quorumSize;
    final AuthenticatedPerfectLink<BaseMessage> link;

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


    public ConcurrentHashMap<Integer, StateMessage> sendReadRequestAndReceiveStates(int currentConsensusID) throws Exception {
        //check if proposed has clientId and sign corrected
        ConditionalCollect<BaseMessage> conditionalCollect = new ConditionalCollect<BaseMessage>(link, quorumSize);

        // we save the conditional collect object to be able to update msg received when receiving msg state
        conditionalCollectByConsensusID.put(currentConsensusID, conditionalCollect);

        conditionalCollect.startCollection(currentConsensusID);
        conditionalCollect.waitForStateMessages();


        //This ensures that leader always send its state message even if other process are quicker to send it
        if(conditionalCollect.getCollectedMessages().get(this.SERVER_ID) == null){
            StateMessage leaderStateMessage = createStateMessage(currentConsensusID);
            conditionalCollect.getCollectedMessages().put(this.SERVER_ID, leaderStateMessage);
        }

        return conditionalCollect.getCollectedMessages();
    }


    public StateMessage createStateMessage(int msgConsensusID) throws Exception {
        SignedWriteset currentWriteset = writesetByConsensusID.get(msgConsensusID);

        if (currentWriteset == null) {
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey(getProcessId()));
            writesetByConsensusID.put(msgConsensusID, currentWriteset);
        }

        SignedValTSPair currentValTsPair = currentValTSPairByConsensusID.get(msgConsensusID);

        System.out.println("SENDING STATUS WITH WRITESET SIZE = " + currentWriteset.getWriteset().size());

        SignedWriteset currentWritesetClone  = new SignedWriteset(currentWriteset); // to garantee that this is an inmutable copy we do clone of this writeset

        StateMessage stateMessageToSend = new StateMessage(this.SERVER_ID, currentValTsPair, currentWritesetClone, msgConsensusID);

        System.out.println("CREATING STATUS WITH == " + stateMessageToSend.prettyPrint());
        return stateMessageToSend;
    }

    public void processReadMessage(ReadMessage msg) throws Exception {

        if(msg.getSenderId() != LEADER_ID ){
            return;
        }

        BaseMessage stateMessage = createStateMessage(msg.getMsgConsensusID()).toBaseMessage(); // SEND IT HAS BASE MSG AND TRY MANUAL TRANSFORM IN RECEIVER
        System.out.println("SENDING STATUS RESPONSE WITH == " + stateMessage.prettyPrint());
        link.sendMessage(stateMessage, 4550);
    }


    public void sendCollectedMsg(ConcurrentHashMap<Integer, StateMessage> collectedStates, int msgConsensusID) throws Exception {
        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<>(link, quorumSize);

        ConcurrentHashMap<Integer, StateMessage>newCollectedStatesDeepCopy = StateMessage.deepCopy(collectedStates); // ensures that no other value is added after the creation of collected msg until all of them are sent


        CollectedMessage collectedMessage = new CollectedMessage(this.SERVER_ID, newCollectedStatesDeepCopy, msgConsensusID);
        System.out.println(CollectedMessage.prettyPrintCollectedMSg(collectedMessage));
        broadcastMessage.sendBroadcast(collectedMessage);
    }



    public SignedValTSPair processCollectedStatesMessage(CollectedMessage collectedMessage, int senderID) {
        //1º value of the most recent ts in byzantine quorum and if value is in writeset f+1
        //3º if none then value of the leader

        System.out.println("IM PROCESSING THE COLLECTED STATES");
        System.out.println(CollectedMessage.prettyPrintCollectedMSg(collectedMessage));
        System.out.println("LEADER WRITESET  = " + collectedMessage.getCollectedStates().get(0).getWriteset().getWriteset().size());


        Map<Integer, StateMessage> collectedStates = collectedMessage.getCollectedStates();


        List<SignedValTSPair> collectedCurrentValues = collectedStates.values()
                .stream()
                .map(StateMessage::getVal)// Extract ValTSPair from each StateMessage
                .filter(signValTSPair -> {
                    try {
                        if (signValTSPair != null) {
                            return signValTSPair.verifySignature(KeyManager.getPublicKey(signValTSPair.getClientId()));
                        } else {
                            return true;
                        }// this might be wrong
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();


        Optional<SignedValTSPair> possibleMaxTsValue = collectedCurrentValues.stream()
                .filter(Objects::nonNull) // Filter out null pairs first (optional)
                .max(Comparator.comparing(
                        (SignedValTSPair pair) -> pair == null ? null : pair.getValTSPair().valTS(),
                        Comparator.nullsFirst(Comparator.naturalOrder()) // Handle null values safely
                ));


        if (possibleMaxTsValue.isPresent()) {

            int maxTsValue = possibleMaxTsValue.get().getValTSPair().valTS();


            collectedCurrentValues = collectedCurrentValues.stream()
                    .filter(pair -> pair != null && pair.getValTSPair() != null) // Ensure no null values
                    .filter(pair -> pair.getValTSPair().valTS() == maxTsValue)  // Compare safely
                    .toList();
            // != maxTsValue.get().valTS());

            List<SignedWriteset> collectedWriteSets = collectedStates.values()
                    .stream()
                    .map(StateMessage::getWriteset)
                    .filter(set -> {
                        try {

                            return set.verifyWriteset(collectedMessage.getSenderId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })  // Extract ValTSPair from each StateMessage
                    .toList();


            for (SignedValTSPair pair : collectedCurrentValues) {
                long numberTimesPairInWriteSet = collectedWriteSets.stream().map(SignedWriteset::getWriteset)
                        .flatMap(List::stream) // Flatten nested lists into a single stream
                        .filter(val -> val.equals(pair)) // Count occurrences of firstValue
                        .count();
                if (numberTimesPairInWriteSet > (quorumSize + 1)) {
                    System.out.println("THE VALUE CHOOSEN IN PROCESS STATSS = " + pair);
                    return pair;
                }
            }
        }

        if(collectedStates.get(0) == null){
            System.out.println("ERROR");
            System.out.println("THE VALUE PROPOSED IN LEADER PROCESS STATUS = NULL");
            collectedMessage.getCollectedStates().forEach((key, value) ->{
                value.getWriteset().getWriteset().forEach((signedValTSPair -> {
                    System.out.print(signedValTSPair.prettyPrint());
                }));
            });

            //return null;
        }
        //System.out.println("THE VALUE CHOOSEN IN PROCESS STATSS = " + collectedMessage.getCollectedStates().get(0).getVal().prettyPrint());
        return collectedMessage.getCollectedStates().get(0).getVal();//collectedStates.get(0).getVal(); // when we have nothing always value that leader has


    }


    public void sendWriteRequest(SignedValTSPair pairToWrite, int msgConsensusID) throws Exception {
        //currentValTSPair = pairToWrite;

        SignedWriteset currentWriteset = writesetByConsensusID.get(msgConsensusID);

        if (currentWriteset == null) {
            currentWriteset = new SignedWriteset(this.SERVER_ID, KeyManager.getPrivateKey(getProcessId()));
            writesetByConsensusID.put(msgConsensusID, currentWriteset);
        }

        System.out.println("--------------------------------UPDATING WRITESET VALUE val = " + pairToWrite.prettyPrint() +  " ----------------------------------------------");

        // update writeSet and current Value to Write in specific consensusID
        currentWriteset.appendToWriteset(pairToWrite);
        currentValTSPairByConsensusID.put(msgConsensusID, pairToWrite);


        BroadcastMessage<BaseMessage> broadcastMessage = new BroadcastMessage<>(link, quorumSize);
        WriteMessage writeMessage = new WriteMessage(this.SERVER_ID, pairToWrite, msgConsensusID);
        broadcastMessage.sendBroadcast(writeMessage);
    }


    public void processWriteRequestAndSendAccept(WriteMessage writeMessage, int msgConsensusID) throws Exception {
        System.out.println("-------------------------------------PROCESSING WRITE MESSAGES --------------------------------------------");


        SignedValTSPair pairToWrite = writeMessage.getPairToProposeWrite();

        if(pairToWrite == null){

            System.out.println("ERROORRR --> leader got a null to write  --> consensus aborted");
            leaderConsensusState.put(msgConsensusID, ConsensusState.Aborted);
            wakeUpConsensusLeader();
            return;
        }

        if (!pairToWrite.verifySignature(KeyManager.getPublicKey(pairToWrite.getClientId()))) {
            System.out.println("---------WRITE MESSAGE INVALID VERIFICATION----------------------------------");
            return;
        }


        ConcurrentHashMap<Integer, Integer> writeRequestsReceived =
                writeRequestsReceivedByConsensusID.computeIfAbsent(msgConsensusID, k -> new ConcurrentHashMap<>());


        writeRequestsReceived.merge(pairToWrite.hashCode(), 1, Integer::sum); // update number of time write request was received
        System.out.println("received " + writeRequestsReceived.get(pairToWrite.hashCode()));
        if (writeRequestsReceived.get(pairToWrite.hashCode()) >= (2 * f + 1) && writeRequestsReceived.get(pairToWrite.hashCode()) != -1) {
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

        System.out.println("-------------------------------------PROCESSING ACCEPT MESSAGES --------------------------------------------");
        SignedValTSPair pairToAccept = acceptMessage.getPairToProposeAccept();

        if (!pairToAccept.verifySignature(KeyManager.getPublicKey(pairToAccept.getClientId()))) {
            System.out.println("---------ACCEPT MESSAGE INVALID VERIFICATION----------------------------------");
            return;
        }

        ConcurrentHashMap<Integer, Integer> acceptRequestsReceived =
                acceptRequestsReceivedByConsensusID.computeIfAbsent(acceptMessage.getMsgConsensusID(), k -> new ConcurrentHashMap<>());

        acceptRequestsReceived.merge(pairToAccept.hashCode(), 1, Integer::sum); // update number of time write request was received

        if (acceptRequestsReceived.get(pairToAccept.hashCode()) >= (2 * f + 1)) {

            SignedValTSPair valueReadyToWrite;
            valueReadyToWrite = pairToAccept;
            acceptRequestsReceived.remove(valueReadyToWrite.hashCode());

            blockchain.addTransactionToProcessAfterConsensus(valueReadyToWrite.getValTSPair().val());
            blockchain.sendConsensusDoneToClient(this.SERVER_ID, acceptMessage.getMsgConsensusID(), valueReadyToWrite.getValTSPair().val(), acceptMessage.getPairToProposeAccept().getClientId());
            //TODO: send message to client saying value was correctly written, leader or not
            System.out.println("Node appendend value " + valueReadyToWrite.getValTSPair().val() + " to the blockchain");
            System.out.println("Confirmation message sent back to client");
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
            SignedValTSPair msg = messagesFromClient.pollFirst();

            if(msg == null){

                System.out.println("---------------INIT MSG FOR CONSENSUS IS NULL ERROR---------------------");
                return;
            }

            currentValTSPairByConsensusID.put(consensusID, msg);

            System.out.println("Value to propose chosen from client = " + currentValTSPairByConsensusID.get(consensusID).prettyPrint());
        }


        ConcurrentHashMap<Integer, StateMessage> collectedStates = sendReadRequestAndReceiveStates(consensusID);

        System.out.println("Received collected Messages");
        System.out.println("Sending Collected Messages for Consensus = " + consensusID);
        sendCollectedMsg(collectedStates, consensusID);
    }

    public void processConsensusRequestMessage(BaseMessage message) throws Exception {

        switch (message.getMessageType()) {
            case READ -> {
                processReadMessage((ReadMessage) message);
            }

            case STATE -> {
                StateMessage msg = (StateMessage) message.toStateMessage();
                System.out.println("COLLECTED RECEIVED == " + msg.prettyPrint() + " || senderID = " + msg.getSenderId());
                if(!msg.getWriteset().getWriteset().isEmpty()){
                    System.out.println("WRITESET ISNT NULLLLL = " + msg.getWriteset().getWriteset().get(0).prettyPrint());
                }

                ConditionalCollect<BaseMessage> conditionalCollect = conditionalCollectByConsensusID.get(message.getMsgConsensusID());
                if (conditionalCollect != null) {
                    conditionalCollect.processStateMessage(message);
                }
            }

            case COLLECTED -> {
                SignedValTSPair pairToProposeWrite = processCollectedStatesMessage((CollectedMessage) message, message.getSenderId());
                sendWriteRequest(pairToProposeWrite, message.getMsgConsensusID());
            }

            case WRITE -> {
                processWriteRequestAndSendAccept((WriteMessage) message, message.getMsgConsensusID());
            }

            case ACCEPT -> {
                processAcceptMessage((AcceptMessage) message);
            }
            case EVMRESULT -> {
                System.out.println("GOT EVM suppose to BE FOR CLIENT ");
                return;
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

