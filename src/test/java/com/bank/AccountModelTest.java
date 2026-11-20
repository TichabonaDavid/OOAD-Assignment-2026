package com.bank;

import com.bank.model.Company;
import com.bank.model.Individual;
import com.bank.model.InvestmentAccount;
import com.bank.model.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountModelTest {
    @Test
    void savingsPaysPointZeroFivePercentAndRejectsWithdrawals() {
        SavingsAccount account = new SavingsAccount("S-1", "Gaborone", 10_000);
        assertEquals(5.00, account.calculateInterest(), 0.001);
        account.payInterest();
        assertEquals(10_005.00, account.getBalance(), 0.001);
        assertThrows(UnsupportedOperationException.class, () -> account.withdraw(1));
    }

    @Test
    void investmentRequiresMinimumOpeningBalanceAndPaysFivePercent() {
        assertThrows(IllegalArgumentException.class, () -> new InvestmentAccount("I-1", "Gaborone", 499.99));
        InvestmentAccount account = new InvestmentAccount("I-2", "Gaborone", 500);
        assertEquals(25.00, account.calculateInterest(), 0.001);
        account.deposit(25);
        assertEquals(525, account.getBalance(), 0.001);
    }

    @Test
    void customersCanHoldSeveralAccountsButChequeAccountsRequireIndividuals() {
        Individual individual = new Individual(1, "one@example.test", "Gaborone", "one", "password",
                "One", "Customer");
        individual.openAccount(new SavingsAccount("S-2", "Gaborone", 100));
        individual.openAccount(new com.bank.model.ChequeAccount("C-2", "Gaborone", 100, "Employer, Gaborone"));
        assertEquals(2, individual.getAccounts().size());
        assertEquals(200, individual.getTotalBalance(), 0.001);

        Company company = new Company(2, "co@example.test", "Gaborone", "company", "password", "Acme", "REG-2");
        assertThrows(IllegalArgumentException.class, () -> company.openAccount(
                new com.bank.model.ChequeAccount("C-3", "Gaborone", 100, "Employer, Gaborone")));
    }
}
