package com.sec.BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public record Transaction(
        String destinationContract,
        String sourceAccount,
        String[] functionAndArgs,
        String signature
) {


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
