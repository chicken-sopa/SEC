// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "./Blacklist.sol";

contract ISTCoin is ERC20, Ownable {
    Blacklist public blacklist;
    uint256 public constant INITIAL_SUPPLY = 100_000_000 * 10 ** 2; // 100 million with 2 decimals

    constructor(address blacklistAddress, address initialOwner) 
        ERC20("IST Coin", "IST") 
        Ownable(initialOwner) 
    {
        // Store reference to already deployed Blacklist contract
        blacklist = Blacklist(blacklistAddress);
        
        // Mint the entire supply to the initial owner
        _mint(initialOwner, INITIAL_SUPPLY);
    }

    function decimals() public pure override returns (uint8) {
        return 2;
    }

    function _update(address from, address to, uint256 value) internal override {
        if (from != address(0)) {
            require(!blacklist.isBlacklisted(from), "ISTCoin: Sender is blacklisted");
        }
        if (to != address(0)) {
            require(!blacklist.isBlacklisted(to), "ISTCoin: Recipient is blacklisted");
        }
        super._update(from, to, value);
    }
}