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
    public static String BlackListContractAddress = "";
    public static String ISTCoinContractAddress = "";
        // BlackList
    public static String addToBlacklistFunctionSignature = "addToBlacklist(address)";
    public static String removeFromBlacklistFunctionSignature = "removeFromBlacklist(address)";
    public static String isBlackListedFunctionSignature = "isBlacklisted(address)";
        // IST Coin
    public static String transferFunctionSignature = "transfer(address,address,uint256)";
    public static String increaseAllowanceFunctionSignature = "increaseAllowance(address,uint256)";
    public static String decreaseAllowanceFunctionSignature = "decreaseAllowance(address,uint256)";
    public static String approveFunctionSignature = "approve(address,uint256)";
    public static String myBalanceFunctionSignature = "myBalance()";

}
