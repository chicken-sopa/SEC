package EVM;

import com.sec.BlockChain.Block;
import com.sec.BlockChain.Transaction;
import org.hyperledger.besu.evm.fluent.EVMExecutor;

public class EVM implements IEVM {

    public EVMExecutor executor;

    public EVM() {
        /// TODO
        /// CREATE GENESIS BLOCK AND PROCESS IT

    }

    @Override
    public void ProcessBlock(Block block) {
        Transaction[] transactions = block.getTransactions();
        for(Transaction transaction : transactions){

        }
    }



}
