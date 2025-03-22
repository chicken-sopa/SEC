package Communication.Collection;

import  com.sec.Links.AuthenticatedPerfectLink;
import  com.sec.Messages.BaseMessage;
import  com.sec.Messages.MessageType;
import  com.sec.Messages.StateMessage;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static Configuration.ProcessConfig.getProcessId;

/**
 * Implementation of the Conditional Collect abstraction.
 * This collects authenticated messages from at least (N - f) processes.
 */
public class ConditionalCollect<T extends BaseMessage> {

    private final AuthenticatedPerfectLink<BaseMessage> link;
    private final int quorumSize;
    private final Map<Integer, StateMessage> collectedMessages = new ConcurrentHashMap<>();
    private final Set<Integer> receivedFrom = Collections.synchronizedSet(new HashSet<>());

    public ConditionalCollect(AuthenticatedPerfectLink<T> link, int quorumSize) {
        this.link = (AuthenticatedPerfectLink<BaseMessage>) link;
        this.quorumSize = quorumSize;
    }

    /**
     * Initiates the Conditional Collect by sending a request to all processes.
     */
    public void startCollection(int currentConsensusID) throws Exception {
        T collectRequest = (T) new InitCollectMessage(getProcessId(), currentConsensusID);
        for (int i = 0; i <= 3; i++) {
            System.out.println("A enviar para o port " + (4550 + i));
            link.sendMessage(collectRequest, 4550 + i);
        }
    }

    /**
     * Processes incoming collect responses.
     */
    public void waitForStateMessages() throws Exception {
        while (collectedMessages.size() < quorumSize) {

        }
        System.out.println("Received quorum of replies");
    }


    public void processStateMessage(BaseMessage received) {
        if (received == null) return; //signature couldn't be verified
        //TODO: this might be incorrect, we might not be supposed to drop this

        int sender = received.getSenderId();
        if (received.getMessageType() == MessageType.STATE) {
            StateMessage receivedState = (StateMessage) received;


            // Store the valid message if it's from a new sender
            if (receivedFrom.add(sender)) {
                collectedMessages.put(sender, receivedState);
            }
        }
    }

    /**
     * Returns the collected messages once the quorum is reached.
     */
    public Map<Integer, StateMessage> getCollectedMessages() {
        return collectedMessages;
    }

    /**
     * Checks whether the collected messages satisfy a specific condition.
     */
    public void isValidCollection() {
    }

}
