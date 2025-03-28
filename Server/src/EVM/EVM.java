package EVM;

import EVM.Genesis.*;
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
import java.math.BigInteger;

public class EVM implements IEVM {

//    public EVMExecutor executor;
    private SimpleWorld world;
    private ByteArrayOutputStream byteArrayOutputStream;
    private StandardJsonTracer tracer;
    private EVMExecutor evmExecutor;

    public EVM() {


        // World creation
        world = new SimpleWorld();

        // Streams
        byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        tracer = new StandardJsonTracer(printStream, true, true, true, true);

        // Genesis
        GenesisBlock genesisblock = GenesisBlock.readGenesisBlockFromJson();
        evmExecutor = EVMExecutor.evm(EvmSpecVersion.CANCUN);


        genesisblock.getState().forEach((addressString, accountState) -> {
            Address address = Address.fromHexString(addressString);
            world.createAccount(address,0, Wei.fromEth(accountState.getBalance()));

            if (accountState.getCode() != null){
                // This is the contract
                Address ownerAddress = Address.fromHexString(genesisblock.blockChainOwnerAddress);
                evmExecutor
                        .tracer(tracer)
                        .code(Bytes.fromHexString(accountState.getCode() + AuxFunctions.padHexStringTo256Bit(ownerAddress.toUnprefixedHexString())))
                        .sender(ownerAddress)
                        .receiver(address)
                        .contract(address)
                        .messageFrameType(MessageFrame.Type.CONTRACT_CREATION)
                        .worldUpdater(world.updater())
                        .commitWorldState();
                System.out.println("Contract deploy code loaded to EVM");

                evmExecutor.callData(Bytes.EMPTY);
                evmExecutor.execute();
                System.out.println("Contract constructor ran");

                String runtimeCode = AuxFunctions.extractWordFromReturnData(byteArrayOutputStream);
                evmExecutor.code(Bytes.fromHexString(runtimeCode))
                        .messageFrameType(MessageFrame.Type.MESSAGE_CALL)
                        .worldUpdater(world.updater())
                        .commitWorldState();
                System.out.println("Contract runtime code loaded to EVM");
            }

        });
    }

    @Override
    public void processBlock(Block block, IEVMClientResponse respondingToClientMethod) {
        Transaction[] transactions = block.getTransactions();
        for(Transaction transaction : transactions){
            processTransaction(transaction, respondingToClientMethod);
        }
    }

    private void processTransaction(Transaction transaction, IEVMClientResponse respondingToClientMethod) {
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


        String answer;
        if (transaction.functionAndArgs()[0].equals(Constants.myBalanceFunctionSignature)){
            // Returns an integer
            BigInteger myBalance = AuxFunctions.extractBigIntegerFromReturnData(byteArrayOutputStream);
            answer = myBalance.toString();
        }
        else{
            // Returns a boolean
            boolean response = AuxFunctions.extractBooleanFromReturnData(byteArrayOutputStream);
            answer = String.valueOf(response);
        }
        respondingToClientMethod.sendEVMAnswerToClient(answer);
    }


}
