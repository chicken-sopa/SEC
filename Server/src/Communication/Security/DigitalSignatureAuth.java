package Communication.Security;

import Communication.Helpers.Constants;
import Communication.Messages.UdpMessage;

import java.security.*;
import java.util.Base64;

public class DigitalSignatureAuth {

    public final KeyPairGenerator keyGen;

    public DigitalSignatureAuth() throws NoSuchAlgorithmException {
        keyGen = KeyPairGenerator.getInstance(Constants.getAlgorithm());
        keyGen.initialize(Constants.getKeySize());
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        return keyGen.generateKeyPair();
    }

    public String signMessage(UdpMessage msg, PrivateKey pKey) throws Exception{
        Signature signature = Signature.getInstance(Constants.getAlgorithm());
        signature.initSign(pKey);
        signature.update(msg.serializeMessage());

        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }

    public boolean verifySignature(UdpMessage msg, PublicKey pubKey, String signatureStr) throws Exception{
        Signature signature = Signature.getInstance(Constants.getAlgorithm());
        signature.initVerify(pubKey);
        signature.update(msg.serializeMessage());

        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }

}
