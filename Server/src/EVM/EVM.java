package EVM;

import EVM.Constants.EVMConstants;
import com.sec.BlockChain.AuxFunctions;
import com.sec.BlockChain.Block;
import com.sec.BlockChain.Transaction;
import com.sec.Helpers.Constants;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class EVM implements IEVM {

//    public EVMExecutor executor;
    private SimpleWorld world;
    private ByteArrayOutputStream byteArrayOutputStream;
    private StandardJsonTracer tracer;
    private EVMExecutor evmExecutor;

    public EVM() {
        // Instantiation
        world = new SimpleWorld();
        byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        tracer = new StandardJsonTracer(printStream, true, true, true, true);

        for(String addressString : Constants.allAddressStrings){
            world.createAccount(addressString, 0, Wei.fromEth(0));
        }

        evmExecutor = EVMExecutor.evm(EvmSpecVersion.CANCUN);

        Address ownerAddress = Address.fromHexString(Constants.owner);
        Address contractAddress = Address.fromHexString(Constants.IstCoin);
        // Setup
        // TODO - Check constructor args after contract refactor
            // Deploy byteCode
        evmExecutor
            .tracer(tracer)
            .code(Bytes.fromHexString(EVMConstants.DeployByteCode + AuxFunctions.padHexStringTo256Bit(ownerAddress.toUnprefixedHexString())))
            .sender(ownerAddress)
            .receiver(contractAddress)
            .contract(contractAddress)
            .messageFrameType(MessageFrame.Type.CONTRACT_CREATION)
            .worldUpdater(world.updater())
            .commitWorldState();
        System.out.println("Contract deploy code loaded to EVM");

        evmExecutor.callData(Bytes.EMPTY);
        evmExecutor.execute();
        System.out.println("Contract constructor ran");

            // RunTime byteCode
        evmExecutor.code(Bytes.fromHexString(EVMConstants.RunTimeByteCode))
                .sender(ownerAddress)
                .receiver(contractAddress)
                .messageFrameType(MessageFrame.Type.MESSAGE_CALL)
                .worldUpdater(blockChain.updater())
                .commitWorldState();
        System.out.println("Contract runtime code loaded to EVM");

        /// TODO
        /// CREATE GENESIS BLOCK AND PROCESS IT

    }

    @Override
    public void processBlock(Block block) {
        Transaction[] transactions = block.getTransactions();
        for(Transaction transaction : transactions){
            processTransaction(transaction);
        }
    }

    private void processTransaction(Transaction transaction) {
        Address senderAddress = Address.fromHexString(transaction.sourceAccount());
        Address contractAddress = Address.fromHexString(transaction.destinationContract());

        StringBuilder hexStringBuilder = new StringBuilder();
        StringBuilder humanReadableStringBuilder = new StringBuilder();
        humanReadableStringBuilder.append("received transaction for SM method <")
                .append(transaction.functionAndArgs()[0])
                .append("> with args ");

        for (int i = 0; i < transaction.functionAndArgs().length; i++){
            if(i == 0)
                hexStringBuilder.append(AuxFunctions.getFunctionSelector(transaction.functionAndArgs()[i]));
            else{
                hexStringBuilder.append(AuxFunctions.padHexStringTo256Bit(transaction.functionAndArgs()[i]));
                humanReadableStringBuilder.append("<")
                        .append(transaction.functionAndArgs()[i])
                        .append("> ");
            }
        }

        System.out.println(humanReadableStringBuilder);

        evmExecutor
            .sender(senderAddress)
            .receiver(contractAddress)
            .worldUpdater(blockChain.updater())
            .commitWorldState();


        evmExecutor.callData(Bytes.fromHexString(hexStringBuilder.toString()));
        evmExecutor.execute();
        System.out.println("Transaction processed");
        // TODO - what do we do to catch responses?
    }


}
