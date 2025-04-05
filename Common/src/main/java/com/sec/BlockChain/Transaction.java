package com.sec.BlockChain;

import com.sec.Keys.KeyManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class Transaction implements Serializable {

    String destinationAddress;
    String sourceAccount;
    String[] functionAndArgs;
    String amount;
    int transactionOwnerId;
    String signature;

    public String destinationAddress() {
        return destinationAddress;
    }

    public String sourceAccount() {
        return sourceAccount;
    }

    public String[] functionAndArgs() {
        return functionAndArgs;
    }

    public String amount() {
        return amount;
    }

    public int transactionOwnerId() {
        return transactionOwnerId;
    }

    public String signature() {
        return signature;
    }

    public Transaction(String destinationAddress, String sourceAccount, String[] functionAndArgs, String amount, int transactionOwnerId) throws Exception {
        this.destinationAddress = destinationAddress;
        this.sourceAccount = sourceAccount;
        this.functionAndArgs = functionAndArgs;
        this.amount = amount;
        this.transactionOwnerId = transactionOwnerId;

        this.signature = signTransaction(KeyManager.getEOAPrivateKey(sourceAccount));
    }





    public byte[] getBytes() throws IOException {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(this);
            return bos.toByteArray();
        }
    }

    public String prettyPrint(){
        StringBuilder sb = new StringBuilder();
        sb.append("Destination Address: ").append(destinationAddress).append("\n");
        sb.append("Source Account: ").append(sourceAccount).append("\n");
        sb.append("Function And Arguments: ").append(functionAndArgs).append("\n");
        sb.append("Amount: ").append(amount).append("\n");
        sb.append("Signature: ").append(signature).append("\n");
        return sb.toString();
    }

    public String signTransaction(PrivateKey privateKey) throws Exception {
        if(Objects.equals(sourceAccount, "0x583031D1113aD414F02576BD6afaBfb302140225")){
            System.out.println("!!!!!!!!!!!!!!!!! swapped private key when signing");
            privateKey = KeyManager.getEOAPrivateKey("CA35b7d915458EF540aDe6068dFe2F44E8fa733c");
        }
        Signature rsa = Signature.getInstance("SHA256withRSA");

        rsa.initSign(privateKey);

        byte[] hashBytes = ByteBuffer.allocate(4).putInt(this.hashCode()).array();  // Convert hashCode to bytes

        rsa.update(hashBytes);

        byte[] signatureBytes = rsa.sign();

        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    @Override
    public int hashCode(){
        return Objects.hash(destinationAddress(), sourceAccount(), amount(), Arrays.hashCode(functionAndArgs()));
    }



}
