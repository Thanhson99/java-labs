package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the state changes and invariants of {@link BankAccount}.
 */
class BankAccountTest {

    /**
     * Confirms that valid money movements update the balance exactly once.
     */
    @Test
    void depositAndWithdrawUpdateBalance() {
        BankAccount account = new BankAccount("ACC-1", "Alice", 100.0);

        account.deposit(25.0);
        account.withdraw(40.0);

        assertEquals(85.0, account.balance());
    }

    /**
     * Documents that an account protects itself from overdraft.
     */
    @Test
    void withdrawRejectsInsufficientFunds() {
        BankAccount account = new BankAccount("ACC-1", "Alice", 100.0);
        assertThrows(IllegalStateException.class, () -> account.withdraw(150.0));
    }

    /**
     * Confirms that the explicit summary method exposes useful human-readable fields.
     */
    @Test
    void summaryContainsUsefulFields() {
        BankAccount account = new BankAccount("ACC-1", "Alice", 100.0);
        assertTrue(account.summary().contains("Alice"));
        assertTrue(account.summary().contains("100.00"));
    }
}
