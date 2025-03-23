package com.sec.BlockChain;

public record Transaction(
        String destinationContract,
        String sourceAccount,
        String[] functionAndArgs,
        String signature
) {

}
