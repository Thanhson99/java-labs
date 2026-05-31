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
     * @throws IllegalArgumentException when the account number, owner name, or balance is invalid
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
     * @throws IllegalArgumentException when {@code amount} is zero or negative
     */
    public void deposit(double amount) {
        validatePositiveAmount(amount);
        balance += amount;
    }

    /**
     * Removes money from the balance when sufficient funds exist.
     *
     * @param amount the amount to withdraw, which must be positive and not exceed the balance
     * @throws IllegalArgumentException when {@code amount} is zero or negative
     * @throws IllegalStateException when the account does not have enough balance
     */
    public void withdraw(double amount) {
        validatePositiveAmount(amount);
        if (amount > balance) {
            throw new IllegalStateException("insufficient funds");
        }
        balance -= amount;
    }

    /**
     * Returns the current account balance.
     *
     * <p>The value is exposed as a read-only query method. Callers must use {@link #deposit(double)}
     * or {@link #withdraw(double)} to change the balance so the account invariants remain protected.</p>
     *
     * @return current balance
     */
    public double balance() {
        return balance;
    }

    /**
     * Creates a short human-readable account summary.
     *
     * <p>This is intentionally separate from {@code toString()} so learners can see how explicit
     * domain methods make output intent clearer in tests and demos.</p>
     *
     * @return formatted account summary with two decimal places for the balance
     */
    public String summary() {
        return "BankAccount{accountNumber='%s', ownerName='%s', balance=%.2f}"
                .formatted(accountNumber, ownerName, balance);
    }

    /**
     * Validates money movements before mutating balance.
     *
     * @param amount amount supplied to a deposit or withdrawal operation
     * @throws IllegalArgumentException when {@code amount} is zero or negative
     */
    private void validatePositiveAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
