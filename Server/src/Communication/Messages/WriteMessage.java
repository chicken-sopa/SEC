package Communication.Messages;

import Communication.Types.ValTSPair.SignedValTSPair;

public class WriteMessage extends BaseMessage{
     SignedValTSPair pairToProposeWrite;

    public WriteMessage(int senderId, SignedValTSPair pairToProposeWrite ) {
        super(MessageType.WRITE, senderId );
        this.pairToProposeWrite = pairToProposeWrite;
    }

    public SignedValTSPair getPairToProposeWrite() {
        return pairToProposeWrite;
    }
}


