package com.sec.Messages.Types.ValTSPair;

import java.io.Serializable;

import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Represents a digitally signed (valTS, val) pair in Byzantine Epoch Consensus.
 */
public class SignedValTSPair implements Serializable {
    private final ValTSPair valTSPair;
    private final String signature;
    private final int clientId;

    public SignedValTSPair(int valTS, String val, int clientId, String signature) throws Exception {
        this.valTSPair = new ValTSPair(val, valTS);
        this.clientId = clientId;
        this.signature = signature;
    }

    public int getClientId() {
        return clientId;
    }

    public String getSignature() {
        return signature;
    }

    public ValTSPair getValTSPair() {
        return valTSPair;
    }

    /**
     * Verifies if the signature is valid using the sender's public key.
     */
    public boolean verifySignature(PublicKey publicKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);
        sign.update(valTSPair.val().getBytes());
        return sign.verify(Base64.getDecoder().decode(signature));
    }
}
