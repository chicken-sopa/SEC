package EVM;

import com.sec.BlockChain.Block;

public interface IEVM {

    public void processBlock(Block block, IEVMClientResponse respondingToClientMethod);
}
