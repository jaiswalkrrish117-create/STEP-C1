package com.gdb.domain;

import com.gdb.exceptions.*;

public abstract class Account {
    private static final int MIN_AGE = 18;
    private static final int MIN_PIN = 1000;
    private static final int MAX_PIN = 9999;

    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String status;
    private Integer pin;

    public abstract double getMinimumBalance();
    public abstract String getAccountType();

    public Account(int accountNumber, String name, int age, double initialBalance) {
        if (age < MIN_AGE) {
            throw new IllegalArgumentException("Customer must be at least 18 years old. Provided: " + age);
        }
        double minBalance = getMinimumBalance();
        if (initialBalance < minBalance) {
            throw new IllegalArgumentException(getAccountType() + " account requires minimum balance of ₹" + minBalance + ". Provided: ₹" + initialBalance);
        }
        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age;
        this.balance = initialBalance;
        this.status = "Active";
        this.pin = null;
    }

    public void deposit(double amount) throws InvalidAmountException, InactiveAccountException {
        validateActive();
        validateAmount(amount);
        balance += amount;
    }

    public void withdraw(double amount, int pin) throws InvalidAmountException, InsufficientBalanceException,
            MinimumBalanceViolationException, InactiveAccountException, InvalidPinException {
        validateActive();
        validatePin(pin);
        validateAmount(amount);
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient balance. Available: ₹" + balance + ", Requested: ₹" + amount);
        }
        double newBalance = balance - amount;
        if (newBalance < getMinimumBalance()) {
            throw new MinimumBalanceViolationException("Cannot withdraw. Minimum balance of ₹" + getMinimumBalance() +
                    " required. Available after withdrawal: ₹" + newBalance);
        }
        balance = newBalance;
    }

    public void closeAccount() {
        if (status.equals("Inactive")) throw new IllegalStateException("Account is already closed");
        status = "Inactive";
    }

    public void reopenAccount() {
        if (status.equals("Active")) throw new IllegalStateException("Account is already active");
        status = "Active";
    }

    public void setPin(int pin) {
        if (pin < MIN_PIN || pin > MAX_PIN) throw new IllegalArgumentException("PIN must be a 4-digit number");
        this.pin = pin;
    }

    public boolean verifyPin(int pin) { return this.pin != null && this.pin == pin; }
    public boolean hasPin() { return pin != null; }

    protected void validateActive() throws InactiveAccountException {
        if (status.equals("Inactive")) throw new InactiveAccountException("Account is inactive. Please reopen the account or contact support.");
    }

    protected void validatePin(int enteredPin) throws InvalidPinException {
        if (!hasPin()) throw new InvalidPinException("PIN not set for this account");
        if (!verifyPin(enteredPin)) throw new InvalidPinException("Incorrect PIN");
    }

    protected void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Amount must be positive. Provided: ₹" + amount);
    }

    protected void setBalance(double balance) { this.balance = balance; }
    protected void addToBalance(double amount) { this.balance += amount; }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}
