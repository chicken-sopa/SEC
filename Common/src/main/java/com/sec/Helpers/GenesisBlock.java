package com.sec.Helpers;

import com.sec.BlockChain.Transaction;
import org.hyperledger.besu.evm.account.MutableAccount;

public class GenesisBlock {
    public String previousBlockHash;
    public Transaction[] transactions;
    public MutableAccount[] state;

    private GenesisBlock() {}
}
