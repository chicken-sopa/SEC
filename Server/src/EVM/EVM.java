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
import org.hyperledger.besu.evm.account.MutableAccount;
import org.hyperledger.besu.evm.fluent.EVMExecutor;
import org.hyperledger.besu.evm.fluent.SimpleWorld;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.tracing.StandardJsonTracer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

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
            world.createAccount(address, 0, Wei.fromEth(accountState.getBalance()));

            if (accountState.getCode() != null) {
                // This is the contract
                Address ownerAddress = Address.fromHexString(genesisblock.getBlockChainOwnerAddress());
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
        for (Transaction transaction : transactions) {
            if (transaction != null) {
                processTransaction(transaction, respondingToClientMethod);
            }
        }
    }

    private void processTransaction(Transaction transaction, IEVMClientResponse respondingToClientMethod) {
        Address senderAddress = Address.fromHexString(transaction.sourceAccount());
        Address destinationAddress = Address.fromHexString(transaction.destinationAddress());

        StringBuilder hexStringBuilder = new StringBuilder();
        StringBuilder humanReadableStringBuilder = new StringBuilder();
        String answer;

        if (!Objects.equals(transaction.amount(), "")) {
            humanReadableStringBuilder.append("received transaction for DC transfer from <")
                    .append(transaction.sourceAccount())
                    .append("> to <")
                    .append(transaction.destinationAddress())
                    .append("> the amount <")
                    .append(transaction.amount())
                    .append(">");

            int transferAmount = Integer.parseInt(transaction.amount());
            MutableAccount senderAccount = world.getAccount(senderAddress);
            if (transferAmount > 0 &&
                    new BigDecimal(senderAccount.getBalance().getValue().toString())
                            .compareTo(new BigDecimal((Wei.fromEth(transferAmount).getValue().toString()))) > 0) {
                // Sender wallet has enough to perform transfer
                senderAccount.decrementBalance(Wei.fromEth(transferAmount));
                world.getAccount(destinationAddress).incrementBalance(Wei.fromEth(transferAmount));
                System.out.println("Transaction finished");
                answer = String.valueOf(true);
            } else {
                System.out.println("you are poor, nothing happened");
                answer = String.valueOf(false);
            }

        } else {
            humanReadableStringBuilder.append("received transaction for SM method <")
                    .append(transaction.functionAndArgs()[0])
                    .append("> with args ");

            for (int i = 0; i < transaction.functionAndArgs().length; i++) {
                if (i == 0)
                    hexStringBuilder.append(AuxFunctions.getFunctionSelector(transaction.functionAndArgs()[i]));
                else {
                    hexStringBuilder.append(AuxFunctions.padHexStringTo256Bit(transaction.functionAndArgs()[i]));
                    humanReadableStringBuilder.append("<")
                            .append(transaction.functionAndArgs()[i])
                            .append("> ");
                }
            }

            System.out.println(humanReadableStringBuilder);

            evmExecutor
                    .sender(senderAddress)
                    .receiver(destinationAddress)
                    .worldUpdater(world.updater())
                    .commitWorldState();


            evmExecutor.callData(Bytes.fromHexString(hexStringBuilder.toString()));
            evmExecutor.execute();
            System.out.println("Transaction processed");


            if (transaction.functionAndArgs()[0].equals(Constants.myBalanceFunctionSignature)) {
                // Returns an integer
                BigInteger myBalance = AuxFunctions.extractBigIntegerFromReturnData(byteArrayOutputStream);
                answer = myBalance.toString();
            } else {
                // Returns a boolean
                boolean response = AuxFunctions.extractBooleanFromReturnData(byteArrayOutputStream);
                answer = String.valueOf(response);
            }
        }
        respondingToClientMethod.sendEVMAnswerToClient(answer);
    }


}
