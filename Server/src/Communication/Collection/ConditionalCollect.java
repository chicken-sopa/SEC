package Communication.Collection;

import Communication.Links.AuthenticatedPerfectLink;
import Communication.Links.Data.MessageDeliveryTuple;
import Communication.Links.LinkMessages.UdpLinkMessage;
import Communication.Collection.BaseMessage;


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
    public void startCollection(int epochId) throws Exception {
        T collectRequest = (T) new InitCollectMessage(getProcessId());
        for (int i = 0;  i <= 2  ;i++) {
            System.out.println("A enviar para o port " + (4550 + i));
            link.sendMessage(collectRequest, 4550 + i);
        }
    }

    /**
     * Processes incoming collect responses.
     */
    public void receiveMessages() throws Exception {
        while (collectedMessages.size() < quorumSize) {
                BaseMessage received =  link.receiveMessage();
            if (received == null ) continue; //signature couldnt be verified
            //TODO: this might be incorrect, we might not be supposed to drop this
            int sender = received.getSenderId();
            if(received.messageType == MessageType.STATE) {
                StateMessage receivedState = (StateMessage) received;


                // Store the valid message if it's from a new sender
                if (receivedFrom.add(sender)) {
                    collectedMessages.put(sender, receivedState);
                }
            }
        }
        System.out.println("Received quorum of replies");
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
