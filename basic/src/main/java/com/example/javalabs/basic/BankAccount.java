package com.example.javalabs.basic;

/**
 * Simple domain object used to demonstrate classes, state, invariants, and exceptions.
 */
public final class BankAccount {

    private final String accountNumber;
    private final String ownerName;
    private double balance;

    /**
     * Creates a new account with an initial balance.
     *
     * @param accountNumber the unique account identifier
     * @param ownerName the account owner's display name
     * @param openingBalance the initial balance, which must be zero or greater
     */
    public BankAccount(String accountNumber, String ownerName, double openingBalance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber must not be blank");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("ownerName must not be blank");
        }
        if (openingBalance < 0) {
            throw new IllegalArgumentException("openingBalance must not be negative");
        }

        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = openingBalance;
    }

    /**
     * Adds money to the current balance.
     *
     * @param amount the amount to deposit, which must be positive
     */
    public void deposit(double amount) {
        validatePositiveAmount(amount);
        balance += amount;
    }

    /**
     * Removes money from the balance when sufficient funds exist.
     *
     * @param amount the amount to withdraw, which must be positive and not exceed the balance
     */
    public void withdraw(double amount) {
        validatePositiveAmount(amount);
        if (amount > balance) {
            throw new IllegalStateException("insufficient funds");
        }
        balance -= amount;
    }

    public double balance() {
        return balance;
    }

    public String summary() {
        return "BankAccount{accountNumber='%s', ownerName='%s', balance=%.2f}"
                .formatted(accountNumber, ownerName, balance);
    }

    private void validatePositiveAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
