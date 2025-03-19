// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./IERC20.sol";

/**
 * @title ISTCoin
 * @dev Basic implementation of an ERC-20 token based on IERC20.
 *      This contract follows the OpenZeppelin design but avoids unnecessary utilities
 *      to ensure clarity and customization.
 */
contract ISTCoin is IERC20 {
    // Mapping of balances for each account
    mapping(address => uint256) private _balances;
    
    // Allowances for delegated transfers
    mapping(address => mapping(address => uint256)) private _allowances;

    // Total token supply
    uint256 private _totalSupply;
    
    // Token metadata
    string public name = "IST Coin";
    string public symbol = "IST";
    uint8 public decimals = 18; // Standard for ERC-20
    
    /**
     * @dev Constructor to set the initial supply and assign it to the deployer.
     * NOTE: Consider whether you want a fixed supply or a minting mechanism.
     */
    constructor() {                
        // Set initial balance and total token supply.
        uint256 initialSupply = 100_000_000 * 10**decimals; 
        _mint(msg.sender, initialSupply);
    }
    
    /**
     * @dev Returns the total supply of tokens.
     */
    function totalSupply() external view override returns (uint256) {
        return _totalSupply;
    }
    
    /**
     * @dev Returns the token balance of a specific account.
     */
    function balanceOf(address account) external view override returns (uint256) {
        return _balances[account];
    }
    
    /**
     * @dev Transfers tokens to a recipient.
     * IMPORTANT: Expand this function with validation logic if needed (e.g., blacklist checks).
     */
    function transfer(address recipient, uint256 amount) external override returns (bool) {
        _transfer(msg.sender, recipient, amount);
        return true;
    }
    
    /**
     * @dev Returns the current allowance between owner and spender.
     */
    function allowance(address owner, address spender) external view override returns (uint256) {
        return _allowances[owner][spender];
    }
    /**
     * @dev Increases the allowance on the caller for this `spender` by given amount.
     */
    function increaseAllowance(address spender, uint256 addedValue) external override  returns (bool) {
        _approve(msg.sender, spender, _allowances[msg.sender][spender] + addedValue);
        return true;
    }

    function decreaseAllowance(address spender, uint256 decreasedValue) external override  returns (bool) {
        _approve(msg.sender, spender, (_allowances[msg.sender][spender]-decreasedValue));
        return true;
    }
    
    /**
     * @dev Transfers tokens from `sender` to `recipient` based on allowance.
     */
    function transferFrom(address sender, address recipient, uint256 amount) external override returns (bool) {
        _transfer(sender, recipient, amount);
        _approve(sender, msg.sender, _allowances[sender][msg.sender] - amount);
        return true;
    }
    
    /**
     * @dev Internal function for handling token transfers.
     * NOTE: This is a core function—expand it to add additional conditions (e.g., fees, blacklist checks).
     */
    function _transfer(address sender, address recipient, uint256 amount) internal {
        require(sender != address(0), "ERC20: transfer from zero address");
        require(recipient != address(0), "ERC20: transfer to zero address");
        require(_balances[sender] >= amount, "ERC20: transfer amount exceeds balance");
        
        _balances[sender] -= amount;
        _balances[recipient] += amount;
        
        emit Transfer(sender, recipient, amount);
    }
    
    /**
     * @dev Internal function to mint new tokens.
     * NOTE: Should this function be public or restricted? Consider adding an access control mechanism.
     */
    function _mint(address account, uint256 amount) internal {
        require(account != address(0), "ERC20: mint to zero address");
        
        _totalSupply += amount;
        _balances[account] += amount;
        
        emit Transfer(address(0), account, amount);
    }
    
    /**
     * @dev Internal function to set allowances.
     */
    function _approve(address owner, address spender, uint256 amount) internal {
        require(owner != address(0), "ERC20: approve from zero address");
        require(spender != address(0), "ERC20: approve to zero address");
        
        _allowances[owner][spender] = amount;
        emit Approval(owner, spender, amount);
    }
}
