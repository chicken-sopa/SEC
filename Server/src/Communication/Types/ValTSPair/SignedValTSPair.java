package Communication.Types.ValTSPair;

import Communication.Types.ValTSPair.ValTSPair;

import java.io.Serializable;
import java.security.PrivateKey;
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

    public SignedValTSPair(int valTS, String val, int clientId, PrivateKey privateKey) throws Exception {
        this.valTSPair = new ValTSPair(val, valTS);
        this.clientId = clientId;
        this.signature = generateSignature(privateKey);
    }

    private String generateSignature(PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update((valTSPair.valTS() + valTSPair.val()).getBytes());
        return Base64.getEncoder().encodeToString(sign.sign());
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
        sign.update((valTSPair.valTS() + valTSPair.val()).getBytes());
        return sign.verify(Base64.getDecoder().decode(signature));
    }
}
