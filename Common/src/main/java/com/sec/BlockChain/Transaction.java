package com.sec.BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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

}
