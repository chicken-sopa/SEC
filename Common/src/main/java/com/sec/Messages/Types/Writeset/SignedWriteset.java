package com.sec.Messages.Types.Writeset;

import com.sec.Messages.Types.ValTSPair.SignedValTSPair;
import com.sec.Keys.KeyManager;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Represents a digitally signed Writeset, extending the regular Writeset.
 * Ensures both individual entries and the full writeset remain untampered.
 */
public class SignedWriteset extends Writeset implements Serializable {
    private String signature;
    private final int serverId;

    public SignedWriteset(int serverId, PrivateKey privateKey) throws Exception {
        super();
        this.serverId = serverId;
        this.signature = generateSignature(privateKey);
    }

    /**
     * Generates a digital signature for the writeset using the private key.
     */
    private String generateSignature(PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);

        // Combine all SignedValTSPair data into a single string for signing
        StringBuilder dataToSign = new StringBuilder();
        for (SignedValTSPair pair : getWriteset()) {
            dataToSign.append(pair.getValTSPair().valTS()).append(pair.getValTSPair().val());
        }

        sign.update(dataToSign.toString().getBytes());
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    public int getServerId() {
        return serverId;
    }

    public String getSignature() {
        return signature;
    }

    /**
     * Adds a new SignedValTSPair to the writeset and updates the signature.
     */
    public void appendToWriteset(SignedValTSPair valueWritten, PrivateKey privateKey) throws Exception {
        super.appendToWriteset(valueWritten);
        regenerateSignature(privateKey);
    }

    /**
     * Regenerates the writeset's signature after modification.
     */
    private void regenerateSignature(PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);

        StringBuilder dataToSign = new StringBuilder();
        for (SignedValTSPair pair : getWriteset()) {
            dataToSign.append(pair.getValTSPair().valTS()).append(pair.getValTSPair().val());
        }

        sign.update(dataToSign.toString().getBytes());
        this.signature = Base64.getEncoder().encodeToString(sign.sign());
    }

    /**
     * Verifies both individual signatures and the full writeset signature.
     */
    public boolean verifyWriteset(int processId) throws Exception {
        // Step 1: Verify each SignedValTSPair
        for (SignedValTSPair valueWritten : getWriteset()) {
            int clientID = valueWritten.getClientId();
            PublicKey publicKey = KeyManager.getPublicKey(clientID);
            if (!valueWritten.verifySignature(publicKey)) {
                return false;
            }
        }

        // Step 2: Verify the writeset's signature
        PublicKey publicKey = KeyManager.getPublicKey(processId);
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);

        // Reconstruct data for verification
        StringBuilder dataToVerify = new StringBuilder();
        for (SignedValTSPair pair : getWriteset()) {
            dataToVerify.append(pair.getValTSPair().valTS()).append(pair.getValTSPair().val());
        }

        sign.update(dataToVerify.toString().getBytes());
        return sign.verify(Base64.getDecoder().decode(signature));
    }
}
