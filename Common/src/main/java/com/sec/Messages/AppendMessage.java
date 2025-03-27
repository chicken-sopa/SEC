package com.sec.Messages;

import com.sec.BlockChain.Transaction;
import com.sec.Keys.KeyManager;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class AppendMessage extends BaseMessage{

    private final Transaction message;

    private final String signature;


    public AppendMessage(MessageType messageType, int senderId, Transaction transaction, int consensusID) throws Exception {
        super(messageType, senderId, consensusID);
        message = transaction;
        this.signature = generateSignature(KeyManager.getPrivateKey(senderId));
    }
    public String getSignature() {
        return signature;
    }
    public Transaction getMessage() {
        return message;
    }

    private String generateSignature(PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes());
        return Base64.getEncoder().encodeToString(sign.sign());
    }
}
