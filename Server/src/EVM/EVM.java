package EVM;

import EVM.Constants.EVMConstants;
import com.sec.BlockChain.AuxFunctions;
import com.sec.BlockChain.Block;
import com.sec.BlockChain.Transaction;
import com.sec.Helpers.Constants;
import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.EvmSpecVersion;
import org.hyperledger.besu.evm.fluent.EVMExecutor;
import org.hyperledger.besu.evm.fluent.SimpleWorld;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.tracing.StandardJsonTracer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class EVM implements IEVM {

//    public EVMExecutor executor;
    private SimpleWorld world;
    private ByteArrayOutputStream byteArrayOutputStream;
    private StandardJsonTracer tracer;
    private EVMExecutor evmExecutor;

    public EVM() {
        // World creation
        world = new SimpleWorld();

        // Account creation
        for(String addressString : Constants.allAddressStrings){
            Address address = Address.fromHexString(addressString);
            world.createAccount(address, 0, Wei.fromEth(0));
        }

        // Streams
        byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        tracer = new StandardJsonTracer(printStream, true, true, true, true);

        // Evm Setup
        evmExecutor = EVMExecutor.evm(EvmSpecVersion.CANCUN);

            // Deploy byteCode
        Address ownerAddress = Address.fromHexString(Constants.owner);
        Address contractAddress = Address.fromHexString(Constants.IstCoin);
        // TODO - Check constructor args after contract refactor
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
                .worldUpdater(world.updater())
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
            .worldUpdater(world.updater())
            .commitWorldState();


        evmExecutor.callData(Bytes.fromHexString(hexStringBuilder.toString()));
        evmExecutor.execute();
        System.out.println("Transaction processed");
        // TODO - what do we do to catch responses?
    }


}
