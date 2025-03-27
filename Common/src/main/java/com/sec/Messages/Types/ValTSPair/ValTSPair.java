package com.sec.Messages.Types.ValTSPair;

import com.sec.BlockChain.Transaction;

import java.io.Serializable;

public record ValTSPair(Transaction val, int valTS ) implements Serializable {
}
