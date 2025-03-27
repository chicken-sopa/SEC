package Lib;

public interface ILib {

    // BlackList contract functions
    public void AddToBlackList(String fromAddress, String blackListAddress, int destinationPort) throws Exception;
    public void RemoveToBlackList(String fromAddress, String blackListAddress, int destinationPort) throws Exception;
    public void IsBlackListed(String fromAddress, String blackListAddress, int destinationPort) throws Exception;

    // IST Coin contract functions
    public void Transfer(String fromAddress, String toAddress, int value, int destinationPort) throws Exception;
    public void IncreaseAllowance(String fromAddress, String spenderAddress, int addedValue, int destinationPort) throws Exception;
    public void DecreaseAllowance(String fromAddress, String spenderAddress, int subtractedValue, int destinationPort) throws Exception;
    public void Approve(String fromAddress, String spenderAddress, int amount, int destinationPort) throws Exception;
    public void MyBalance(String fromAddress, int destinationPort) throws Exception;
}
