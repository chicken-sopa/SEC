package com.sec.Messages.Types.Writeset;

import com.sec.Messages.Types.ValTSPair.SignedValTSPair;

import java.io.Serializable;
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


    public Writeset(Writeset other) throws Exception {
        this.writeset = new ArrayList<>();
        for (SignedValTSPair pair : other.writeset) {
            this.writeset.add(new SignedValTSPair(pair)); // Deep copy each SignedValTSPair
        }
    }

}
