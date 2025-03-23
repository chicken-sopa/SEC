// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract Blacklist {
    mapping(address => bool) private _blacklisted;
    address private constant OWNER = 0x78731D3Ca6b7E34aC0F824c42a7cC18A495cabaB; // Hardcoded owner address

    event AddedToBlacklist(address indexed account);
    event RemovedFromBlacklist(address indexed account);

    modifier onlyOwner() {
        require(msg.sender == OWNER, "Blacklist: Caller is not the owner");
        _;
    }

    function addToBlacklist(address account) external onlyOwner returns (bool) {
        _blacklisted[account] = true;
        emit AddedToBlacklist(account);
        return true;
    }

    function removeFromBlacklist(address account) external onlyOwner returns (bool) {
        _blacklisted[account] = false;
        emit RemovedFromBlacklist(account);
        return true;
    }

    function isBlacklisted(address account) external view returns (bool) {
        return _blacklisted[account];
    }
}
