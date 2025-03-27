// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/access/Ownable.sol";

contract Blacklist is Ownable {
    mapping(address => bool) private _blacklisted;

    event AddedToBlacklist(address indexed account);
    event RemovedFromBlacklist(address indexed account);

    constructor(address initialOwner) Ownable(initialOwner) {}

    function addToBlacklist(address account) external onlyOwner returns (bool) {
        require(!_blacklisted[account], "Blacklist: Address already blacklisted");
        _blacklisted[account] = true;
        emit AddedToBlacklist(account);
        return true;
    }

    function removeFromBlacklist(address account) external onlyOwner returns (bool) {
        require(_blacklisted[account], "Blacklist: Address not blacklisted");
        _blacklisted[account] = false;
        emit RemovedFromBlacklist(account);
        return true;
    }

    function isBlacklisted(address account) external view returns (bool) {
        return _blacklisted[account];
    }
}
