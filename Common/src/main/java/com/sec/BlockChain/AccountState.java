package com.sec.BlockChain;

import java.util.Map;

public class AccountState {
    private int balance; // Can be String or BigInteger depending on use-case
    private String code; // Optional, for contract accounts
    private Map<String, String> storage; // Optional, for contract storage

    private AccountState(){}

    // Getters and setters
    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getStorage() {
        return storage;
    }

    public void setStorage(Map<String, String> storage) {
        this.storage = storage;
    }




}
