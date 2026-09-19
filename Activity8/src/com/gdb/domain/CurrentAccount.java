package com.gdb.domain;

import com.gdb.exceptions.*;

public class CurrentAccount extends Account {
    private static final double MINIMUM_BALANCE = 1000.0;
    private static final String ACCOUNT_TYPE = "Current";
    private static final double OVERDRAFT_LIMIT = 5000.0;
    private double overdraftUsed;

    public CurrentAccount(int accountNumber, String name, int age, double initialBalance) {
        super(accountNumber, name, age, initialBalance);
        this.overdraftUsed = 0.0;
    }

    @Override
    public double getMinimumBalance() { return MINIMUM_BALANCE; }

    @Override
    public String getAccountType() { return ACCOUNT_TYPE; }

    @Override
    public void withdraw(double amount, int pin) throws InvalidAmountException, InsufficientBalanceException,
            MinimumBalanceViolationException, InactiveAccountException, InvalidPinException {
        validateActive();
        validatePin(pin);
        validateAmount(amount);

        double availableFunds = getBalance() + getAvailableOverdraft();
        if (amount > availableFunds) {
            throw new InsufficientBalanceException(
                    "Insufficient funds. Available: ₹" + availableFunds +
                    " (including ₹" + OVERDRAFT_LIMIT + " overdraft), Requested: ₹" + amount);
        }

        double newBalance = getBalance() - amount;
        double newOverdraftUsed = newBalance < getMinimumBalance()
                ? getMinimumBalance() - newBalance : 0.0;

        if (newOverdraftUsed > OVERDRAFT_LIMIT) {
            throw new InsufficientBalanceException("Overdraft limit exceeded");
        }

        overdraftUsed = newOverdraftUsed;
        setBalance(newBalance);
    }

    public double getOverdraftLimit() { return OVERDRAFT_LIMIT; }
    public double getOverdraftUsed() { return overdraftUsed; }
    public double getAvailableOverdraft() { return OVERDRAFT_LIMIT - overdraftUsed; }
    public boolean isUsingOverdraft() { return overdraftUsed > 0; }

    public void repayOverdraft(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Repayment amount must be positive");
        if (amount > overdraftUsed) throw new IllegalArgumentException("Amount exceeds overdraft used (₹" + overdraftUsed + ")");
        overdraftUsed -= amount;
        addToBalance(amount);
    }
}
