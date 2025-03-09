package Communication.Types.Writeset;

import Communication.Types.ValTSPair.SignedValTSPair;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class Writeset implements Serializable {

    private ArrayList<SignedValTSPair> writeset;

    public Writeset() {
        writeset = new ArrayList<>();
    }


    public ArrayList<SignedValTSPair> getWriteset() {
        return writeset;
    }

    public void appendToWriteset(SignedValTSPair valueWritten) {
        writeset.add(valueWritten);
    }


}
