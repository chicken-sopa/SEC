package Communication.Messages;

import Communication.Types.ValTSPair.SignedValTSPair;

public class AcceptMessage extends BaseMessage{


    SignedValTSPair pairToProposeAccept;

    public AcceptMessage(int senderId, SignedValTSPair pairToProposeAccept ) {
        super(MessageType.WRITE, senderId );
        this.pairToProposeAccept = pairToProposeAccept;
    }


    public SignedValTSPair getPairToProposeAccept() {
        return pairToProposeAccept;
    }
}