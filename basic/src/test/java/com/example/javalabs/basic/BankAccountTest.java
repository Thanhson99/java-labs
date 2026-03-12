package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankAccountTest {

    @Test
    void depositAndWithdrawUpdateBalance() {
        BankAccount account = new BankAccount("ACC-1", "Alice", 100.0);

        account.deposit(25.0);
        account.withdraw(40.0);

        assertEquals(85.0, account.balance());
    }

    @Test
    void withdrawRejectsInsufficientFunds() {
        BankAccount account = new BankAccount("ACC-1", "Alice", 100.0);
        assertThrows(IllegalStateException.class, () -> account.withdraw(150.0));
    }

    @Test
    void summaryContainsUsefulFields() {
        BankAccount account = new BankAccount("ACC-1", "Alice", 100.0);
        assertTrue(account.summary().contains("Alice"));
        assertTrue(account.summary().contains("100.00"));
    }
}
