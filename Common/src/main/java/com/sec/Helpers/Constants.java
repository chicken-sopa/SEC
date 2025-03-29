package com.sec.Helpers;

public final class Constants {

    private static final String Rsa = "RSA";
    private static final String algorithm = "SHA256withRSA";
    private static final int KeySize = 2048;

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getAlgorithm() {
        return algorithm;
    }

    public static int getKeySize() {
        return KeySize;
    }

    public static String getRsa() {
        return Rsa;
    }

    // BlockChain
    public static final String BlackListContractAddress = "";
    public static final String ISTCoinContractAddress = "0x17F6AD8Ef982297579C203069C1DbfFE4348c372";
        // BlackList
    public static final String addToBlacklistFunctionSignature = "addToBlacklist(address)";
    public static final String removeFromBlacklistFunctionSignature = "removeFromBlacklist(address)";
    public static final String isBlackListedFunctionSignature = "isBlacklisted(address)";
        // IST Coin
    public static final String transferFunctionSignature = "transfer(address,address,uint256)";
    public static final String increaseAllowanceFunctionSignature = "increaseAllowance(address,uint256)";
    public static final String decreaseAllowanceFunctionSignature = "decreaseAllowance(address,uint256)";
    public static final String approveFunctionSignature = "approve(address,uint256)";
    public static final String myBalanceFunctionSignature = "myBalance()";
        // Account addresses
    public static final String owner = "0x78731D3Ca6b7E34aC0F824c42a7cC18A495cabaB";
    public static final String IstCoin = "0x17F6AD8Ef982297579C203069C1DbfFE4348c372";
    public static final String UserA = "0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c";
    public static final String UserB = "0x4B0897b0513fdC7C541B6d9D7E929C4e5364D2dB";
    public static final String UserC = "0x583031D1113aD414F02576BD6afaBfb302140225";
    public static final String[] allAddressStrings = {owner, IstCoin, UserA, UserB, UserC};
        // Genesis
    public static final String genesisLocation = "EVM/Genesis/Genesis.json";


}
