package Communication.Collection;

import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.ValTSPair.ValTSPair;

import java.util.Map;

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


