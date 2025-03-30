package com.sec.Messages.Types.ValTSPair;

import com.sec.BlockChain.Transaction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public record ValTSPair(Transaction val, int valTS ) implements Serializable {


}
