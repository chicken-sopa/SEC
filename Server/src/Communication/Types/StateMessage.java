package Communication.Types;

import Communication.Types.ValTSPair.SignedValTSPair;
import Communication.Types.ValTSPair.ValTSPair;
import Communication.Types.Writeset.SignedWriteset;

import java.io.Serializable;
import java.util.ArrayList;

public class StateMessage implements Serializable {

    SignedValTSPair val;

    SignedWriteset writeset;

    //possivelmente o id sender aqui tambem? mas isso deve ir no envio da propria mensagem

}

