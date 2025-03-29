package Lib;

import com.sec.BlockChain.Transaction;
import com.sec.Messages.BaseMessage;

public interface ILib {

    public void SendAppendMessage(Transaction messageToAppend, int destinationPort) throws Exception;
    public BaseMessage ReceiveMessage() throws Exception;

    // BlackList contract functions
    public Transaction AddToBlackList(String fromAddress, String blackListAddress) throws Exception;
    public Transaction RemoveFromBlackList(String fromAddress, String blackListAddress) throws Exception;
    public Transaction IsBlackListed(String fromAddress, String blackListAddress) throws Exception;

    // IST Coin contract functions
    public Transaction Transfer(String fromAddress, String toAddress, int value) throws Exception;
    public Transaction IncreaseAllowance(String fromAddress, String spenderAddress, int addedValue) throws Exception;
    public Transaction DecreaseAllowance(String fromAddress, String spenderAddress, int subtractedValue) throws Exception;
    public Transaction Approve(String fromAddress, String spenderAddress, int amount) throws Exception;
    public Transaction MyBalance(String fromAddress) throws Exception;
}