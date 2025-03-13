package Communication.Messages;

import Communication.Types.ValTSPair.SignedValTSPair;

public class WriteMessage extends BaseMessage{
     SignedValTSPair pairToProposeWrite;

    public WriteMessage(int senderId, SignedValTSPair pairToProposeWrite, int consensusID ) {
        super(MessageType.WRITE, senderId, consensusID );
        this.pairToProposeWrite = pairToProposeWrite;
    }

    public SignedValTSPair getPairToProposeWrite() {
        return pairToProposeWrite;
    }
}


