// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "./Blacklist.sol";

contract ISTCoin is ERC20, Blacklist {
    uint256 public constant INITIAL_SUPPLY = 100_000_000 * 10 ** 2; // 100 million with 2 decimals

    // Add to track if direct approve calls should be allowed
    bool public directApproveEnabled = false;

    constructor(address initialOwner)
    ERC20("IST Coin", "IST")
    Blacklist(initialOwner)
    {
        // Mint the entire supply to the initial owner
        _mint(initialOwner, INITIAL_SUPPLY);
    }

    function decimals() public pure override returns (uint8) {
        return 2;
    }

    function transfer(address from, address to, uint256 value) public returns (bool) {
        _update(from, to, value);
        return true;
    }

    function _update(address from, address to, uint256 value) internal override {
        if (from != address(0)) {
            require(!isBlacklisted(from), "ISTCoin: Sender is blacklisted");
        }
        super._update(from, to, value);
    }

    /**
     * @dev Increases the allowance granted to `spender` by the caller.
     * This is an alternative to {approve} that can be used as a mitigation for
     * the potential race condition in the ERC20 standard.
     */
    function increaseAllowance(address spender, uint256 addedValue) public virtual returns (bool) {
        require(!isBlacklisted(spender), "ISTCoin: Spender is blacklisted");

        address owner = _msgSender();
        _approve(owner, spender, allowance(owner, spender) + addedValue);
        return true;
    }

    /**
     * @dev Decreases the allowance granted to `spender` by the caller.
     * This is an alternative to {approve} that can be used as a mitigation for
     * the potential race condition in the ERC20 standard.
     */
    function decreaseAllowance(address spender, uint256 subtractedValue) public virtual returns (bool) {
        require(!isBlacklisted(spender), "ISTCoin: Spender is blacklisted");

        address owner = _msgSender();
        uint256 currentAllowance = allowance(owner, spender);
        require(currentAllowance >= subtractedValue, "ERC20: decreased allowance below zero");
        unchecked {
            _approve(owner, spender, currentAllowance - subtractedValue);
        }
        return true;
    }

    /**
     * @dev Override the approve function to add blacklist checks and
     * potentially disable direct approve calls.
     */
    function approve(address spender, uint256 amount) public virtual override returns (bool) {
        require(directApproveEnabled, "ISTCoin: Direct approve calls are disabled");

        return super.approve(spender, amount);
    }
    /**
 * @dev Returns the balance of the caller.
     * A convenience function to check your own balance without passing your address.
     */
    function myBalance() public view returns (uint256) {
        return balanceOf(msg.sender);
    }

}