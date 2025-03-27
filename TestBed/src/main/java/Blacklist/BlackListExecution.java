package Blacklist;

import Helpers.AuxFunctions;
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

public class BlackListExecution {

    // BlackList Function Selectors
    String addToBlacklistFunctionSelector = AuxFunctions.getFunctionSelector("addToBlacklist(address)");
    String removeFromBlacklistFunctionSelector = AuxFunctions.getFunctionSelector("removeFromBlacklist(address)");
    String isBlackListedFunctionSelector = AuxFunctions.getFunctionSelector("isBlacklisted(address)");

    // Addresses
    String owner = "0x78731D3Ca6b7E34aC0F824c42a7cC18A495cabaB";
    String blackList = "0x5B38Da6a701c568545dCfcB03FcB875f56beddC4";
    String dudeToBlackList = "0x17F6AD8Ef982297579C203069C1DbfFE4348c372";

    // BlackList Contract code
    String blackListContractDeployCode = "608060405234801561000f575f80fd5b50604051610a79380380610a79833981810160405281019061003191906101d7565b805f73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16036100a2575f6040517f1e4fbdf70000000000000000000000000000000000000000000000000000000081526004016100999190610211565b60405180910390fd5b6100b1816100b860201b60201c565b505061022a565b5f805f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050815f806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b5f80fd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f6101a68261017d565b9050919050565b6101b68161019c565b81146101c0575f80fd5b50565b5f815190506101d1816101ad565b92915050565b5f602082840312156101ec576101eb610179565b5b5f6101f9848285016101c3565b91505092915050565b61020b8161019c565b82525050565b5f6020820190506102245f830184610202565b92915050565b610842806102375f395ff3fe608060405234801561000f575f80fd5b5060043610610060575f3560e01c806344337ea114610064578063537df3b614610094578063715018a6146100c45780638da5cb5b146100ce578063f2fde38b146100ec578063fe575a8714610108575b5f80fd5b61007e6004803603810190610079919061065a565b610138565b60405161008b919061069f565b60405180910390f35b6100ae60048036038101906100a9919061065a565b61026b565b6040516100bb919061069f565b60405180910390f35b6100cc61039d565b005b6100d66103b0565b6040516100e391906106c7565b60405180910390f35b6101066004803603810190610101919061065a565b6103d7565b005b610122600480360381019061011d919061065a565b61045b565b60405161012f919061069f565b60405180910390f35b5f6101416104ad565b60015f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f9054906101000a900460ff16156101cb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101c290610760565b60405180910390fd5b6001805f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f6101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff167ff9b68063b051b82957fa193585681240904fed808db8b30fc5a2d2202c6ed62760405160405180910390a260019050919050565b5f6102746104ad565b60015f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f9054906101000a900460ff166102fd576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102f4906107ee565b60405180910390fd5b5f60015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f6101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff167f2b6bf71b58b3583add364b3d9060ebf8019650f65f5be35f5464b9cb3e4ba2d460405160405180910390a260019050919050565b6103a56104ad565b6103ae5f610534565b565b5f805f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b6103df6104ad565b5f73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff160361044f575f6040517f1e4fbdf700000000000000000000000000000000000000000000000000000000815260040161044691906106c7565b60405180910390fd5b61045881610534565b50565b5f60015f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f9054906101000a900460ff169050919050565b6104b56105f5565b73ffffffffffffffffffffffffffffffffffffffff166104d36103b0565b73ffffffffffffffffffffffffffffffffffffffff1614610532576104f66105f5565b6040517f118cdaa700000000000000000000000000000000000000000000000000000000815260040161052991906106c7565b60405180910390fd5b565b5f805f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050815f806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b5f33905090565b5f80fd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f61062982610600565b9050919050565b6106398161061f565b8114610643575f80fd5b50565b5f8135905061065481610630565b92915050565b5f6020828403121561066f5761066e6105fc565b5b5f61067c84828501610646565b91505092915050565b5f8115159050919050565b61069981610685565b82525050565b5f6020820190506106b25f830184610690565b92915050565b6106c18161061f565b82525050565b5f6020820190506106da5f8301846106b8565b92915050565b5f82825260208201905092915050565b7f426c61636b6c6973743a204164647265737320616c726561647920626c61636b5f8201527f6c69737465640000000000000000000000000000000000000000000000000000602082015250565b5f61074a6026836106e0565b9150610755826106f0565b604082019050919050565b5f6020820190508181035f8301526107778161073e565b9050919050565b7f426c61636b6c6973743a2041646472657373206e6f7420626c61636b6c6973745f8201527f6564000000000000000000000000000000000000000000000000000000000000602082015250565b5f6107d86022836106e0565b91506107e38261077e565b604082019050919050565b5f6020820190508181035f830152610805816107cc565b905091905056fea26469706673582212207288229610da2ede2f296b02ae934dd3f92f6a15d5d42fe757b52f6c469e9d9a64736f6c634300081a0033";
    String blackListContractRuntimeCode = "608060405234801561000f575f80fd5b5060043610610060575f3560e01c806344337ea114610064578063537df3b614610094578063715018a6146100c45780638da5cb5b146100ce578063f2fde38b146100ec578063fe575a8714610108575b5f80fd5b61007e6004803603810190610079919061065a565b610138565b60405161008b919061069f565b60405180910390f35b6100ae60048036038101906100a9919061065a565b61026b565b6040516100bb919061069f565b60405180910390f35b6100cc61039d565b005b6100d66103b0565b6040516100e391906106c7565b60405180910390f35b6101066004803603810190610101919061065a565b6103d7565b005b610122600480360381019061011d919061065a565b61045b565b60405161012f919061069f565b60405180910390f35b5f6101416104ad565b60015f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f9054906101000a900460ff16156101cb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101c290610760565b60405180910390fd5b6001805f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f6101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff167ff9b68063b051b82957fa193585681240904fed808db8b30fc5a2d2202c6ed62760405160405180910390a260019050919050565b5f6102746104ad565b60015f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f9054906101000a900460ff166102fd576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102f4906107ee565b60405180910390fd5b5f60015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f6101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff167f2b6bf71b58b3583add364b3d9060ebf8019650f65f5be35f5464b9cb3e4ba2d460405160405180910390a260019050919050565b6103a56104ad565b6103ae5f610534565b565b5f805f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b6103df6104ad565b5f73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff160361044f575f6040517f1e4fbdf700000000000000000000000000000000000000000000000000000000815260040161044691906106c7565b60405180910390fd5b61045881610534565b50565b5f60015f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f9054906101000a900460ff169050919050565b6104b56105f5565b73ffffffffffffffffffffffffffffffffffffffff166104d36103b0565b73ffffffffffffffffffffffffffffffffffffffff1614610532576104f66105f5565b6040517f118cdaa700000000000000000000000000000000000000000000000000000000815260040161052991906106c7565b60405180910390fd5b565b5f805f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050815f806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b5f33905090565b5f80fd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f61062982610600565b9050919050565b6106398161061f565b8114610643575f80fd5b50565b5f8135905061065481610630565b92915050565b5f6020828403121561066f5761066e6105fc565b5b5f61067c84828501610646565b91505092915050565b5f8115159050919050565b61069981610685565b82525050565b5f6020820190506106b25f830184610690565b92915050565b6106c18161061f565b82525050565b5f6020820190506106da5f8301846106b8565b92915050565b5f82825260208201905092915050565b7f426c61636b6c6973743a204164647265737320616c726561647920626c61636b5f8201527f6c69737465640000000000000000000000000000000000000000000000000000602082015250565b5f61074a6026836106e0565b9150610755826106f0565b604082019050919050565b5f6020820190508181035f8301526107778161073e565b9050919050565b7f426c61636b6c6973743a2041646472657373206e6f7420626c61636b6c6973745f8201527f6564000000000000000000000000000000000000000000000000000000000000602082015250565b5f6107d86022836106e0565b91506107e38261077e565b604082019050919050565b5f6020820190508181035f830152610805816107cc565b905091905056fea26469706673582212207288229610da2ede2f296b02ae934dd3f92f6a15d5d42fe757b52f6c469e9d9a64736f6c634300081a0033";

    public BlackListExecution() {}

    public void Execute(){

        // World Creation
        SimpleWorld blockChain = new SimpleWorld();

        // Account creation
        Address ownerAddress = Address.fromHexString(owner);
        Address blackListContractAddress = Address.fromHexString(blackList);
        Address dudeGettingBlackListedAddress = Address.fromHexString(dudeToBlackList);

        blockChain.createAccount(ownerAddress, 0, Wei.fromEth(199));
        blockChain.createAccount(dudeGettingBlackListedAddress, 0, Wei.fromEth(10));
        blockChain.createAccount(blackListContractAddress, 0, Wei.fromEth(0));

        // Accounts fetched as Mutable accounts
        MutableAccount ownerAccount = (MutableAccount) blockChain.get(ownerAddress);
        MutableAccount blackListContractAccount = (MutableAccount) blockChain.get(blackListContractAddress);
        MutableAccount blacklistedDudeAccount = (MutableAccount) blockChain.get(dudeGettingBlackListedAddress);


        // Streams
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        StandardJsonTracer tracer = new StandardJsonTracer(printStream, true, true, true, true);

        // Execution
        EVMExecutor ContractExecutor = EVMExecutor.evm(EvmSpecVersion.CANCUN)
                .tracer(tracer)
                .code(Bytes.fromHexString(blackListContractDeployCode + AuxFunctions.padHexStringTo256Bit(ownerAddress.toUnprefixedHexString())))
                .sender(ownerAddress)
                .receiver(blackListContractAddress)
                .contract(blackListContractAddress)
                .messageFrameType(MessageFrame.Type.CONTRACT_CREATION)
                .worldUpdater(blockChain.updater())
                .commitWorldState();
        System.out.println("Contract code loaded");

        ContractExecutor.callData(Bytes.EMPTY);
        ContractExecutor.execute();
        System.out.println("Constructor ran");

        ContractExecutor.code(Bytes.fromHexString(blackListContractRuntimeCode))
                .sender(ownerAddress)
                .receiver(blackListContractAddress)
                .messageFrameType(MessageFrame.Type.MESSAGE_CALL)
                .worldUpdater(blockChain.updater())
                .commitWorldState();

        // Run test battery
        RunBlackListTestBattery(ContractExecutor, dudeGettingBlackListedAddress, byteArrayOutputStream, addToBlacklistFunctionSelector, removeFromBlacklistFunctionSelector, isBlackListedFunctionSelector);
    }

    public static void SetupBlackList(EVMExecutor evm, StandardJsonTracer tracer, Address ownerAddress, Address blackListContractAddress, SimpleWorld world, String blackListContractDeployCode, String blackListContractRuntimeCode){
        // Execution
        evm
                .tracer(tracer)
                .code(Bytes.fromHexString(blackListContractDeployCode + AuxFunctions.padHexStringTo256Bit(ownerAddress.toUnprefixedHexString())))
                .sender(ownerAddress)
                .receiver(blackListContractAddress)
                .contract(blackListContractAddress)
                .messageFrameType(MessageFrame.Type.CONTRACT_CREATION)
                .worldUpdater(world.updater())
                .commitWorldState();
        System.out.println("Contract code for blackList loaded");

        evm.callData(Bytes.EMPTY);
        evm.execute();
        System.out.println("Constructor for blackList ran");

        evm.code(Bytes.fromHexString(blackListContractRuntimeCode))
                .sender(ownerAddress)
                .receiver(blackListContractAddress)
                .messageFrameType(MessageFrame.Type.MESSAGE_CALL)
                .worldUpdater(world.updater())
                .commitWorldState();
        System.out.println("Changed to runtimeCode for blackList");
    }

    public static void RunBlackListTestBattery(EVMExecutor ContractExecutor, Address dudeGettingBlackListedAddress, ByteArrayOutputStream byteArrayOutputStream, String addToBlacklistFunctionSelector, String removeFromBlacklistFunctionSelector, String isBlackListedFunctionSelector){
        // Check if dude is blackListed
        CheckIfDudeIsBlackListed(ContractExecutor, dudeGettingBlackListedAddress, byteArrayOutputStream, isBlackListedFunctionSelector);

        // BlackList the dude
        ContractExecutor.callData(Bytes.fromHexString(addToBlacklistFunctionSelector + AuxFunctions.padHexStringTo256Bit(dudeGettingBlackListedAddress.toUnprefixedHexString())));
        ContractExecutor.execute();
        System.out.println("made call to blackList dude\n");

        // Check if dude is blackListed
        CheckIfDudeIsBlackListed(ContractExecutor, dudeGettingBlackListedAddress, byteArrayOutputStream, isBlackListedFunctionSelector);

        // Remove dude from blackList
        ContractExecutor.callData(Bytes.fromHexString(removeFromBlacklistFunctionSelector + AuxFunctions.padHexStringTo256Bit(dudeGettingBlackListedAddress.toUnprefixedHexString())));
        ContractExecutor.execute();
        System.out.println("made call to un-blackList the dude\n");

        // Check if dude is blackListed
        CheckIfDudeIsBlackListed(ContractExecutor, dudeGettingBlackListedAddress, byteArrayOutputStream, isBlackListedFunctionSelector);
    }

    private static boolean CheckIfDudeIsBlackListed(EVMExecutor ContractExecutor, Address addressToCheck, ByteArrayOutputStream byteArrayOutputStream, String isBlackListedFunctionSelector){
        // Check if dude is blackListed
        ContractExecutor.callData(Bytes.fromHexString(isBlackListedFunctionSelector + AuxFunctions.padHexStringTo256Bit(addressToCheck.toUnprefixedHexString())));
        ContractExecutor.execute();
        System.out.println("made call to check if dude is blackListed");
        boolean callReturn = AuxFunctions.extractBooleanFromReturnData(byteArrayOutputStream);
        System.out.println("DudeGettingBlacklisted is black listed -> " + callReturn+"\n");
        return callReturn;
    }

}
