package com.sec.Links.Security;

import com.sec.Helpers.Auxiliary;
import com.sec.Helpers.Constants;
import com.sec.Links.LinkMessages.Base.Contracts.ILinkMessage;
import com.sec.Links.LinkMessages.Base.Contracts.IMessage;

import java.security.*;
import java.util.Base64;

public class DigitalSignatureAuth<T extends IMessage> {

    public final KeyPairGenerator keyGen;

    public DigitalSignatureAuth() throws NoSuchAlgorithmException {
        keyGen = KeyPairGenerator.getInstance(Constants.getRsa());
        keyGen.initialize(Constants.getKeySize());
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        return keyGen.generateKeyPair();
    }

    public String signMessage(ILinkMessage<T> msg, PrivateKey pKey) throws Exception{
        Signature signature = Signature.getInstance(Constants.getAlgorithm());
        signature.initSign(pKey);
        signature.update(msg.serializeMessage());

        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }

    public boolean verifySignature(ILinkMessage<T> msg, PublicKey pubKey, String signatureStr) throws Exception{
        //System.out.println("THIS IS VERIFICATION  MESSAGE ID =  " + msg.getMessageId() + " || type: " + msg.getType() );
        Signature signature = Signature.getInstance(Constants.getAlgorithm());
        signature.initVerify(pubKey);
        signature.update(msg.serializeMessage());

        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);

        return signature.verify(signatureBytes);
    }

    public String signMessageVal(String msg, PrivateKey pKey) throws Exception{
        Signature signature = Signature.getInstance(Constants.getAlgorithm());
        signature.initSign(pKey);
        signature.update(msg.getBytes());

        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }

}
