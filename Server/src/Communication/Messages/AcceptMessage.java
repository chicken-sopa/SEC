package Communication.Messages;

import Communication.Types.ValTSPair.SignedValTSPair;

public class AcceptMessage extends BaseMessage{


    SignedValTSPair pairToProposeAccept;

    public AcceptMessage(int senderId, SignedValTSPair pairToProposeAccept, int consensusID ) {
        super(MessageType.WRITE, senderId, consensusID );
        this.pairToProposeAccept = pairToProposeAccept;
    }


    public SignedValTSPair getPairToProposeAccept() {
        return pairToProposeAccept;
    }
}