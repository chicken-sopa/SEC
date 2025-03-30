package com.sec.BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public record Transaction(
        // TODO -> Change to destrinationAddress to support DEPCoin transferences
        // Add value -> represents depCoin
        // logic - if value != 0/null, use logic to remove from one account, add to another
        String destinationAddress,
        String sourceAccount,
        String[] functionAndArgs,
        String amount,
        String signature
) implements Serializable {

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

    @Override
    public int hashCode(){
        return Objects.hash(destinationAddress(), sourceAccount(), signature(), amount(), Arrays.hashCode(functionAndArgs()));
    }

}
