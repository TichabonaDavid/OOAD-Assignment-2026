package com.bank;

import com.bank.controller.AccountController;
import com.bank.controller.LoginController;
import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.dao.DatabaseManager;
import com.bank.model.AccountSummary;
import com.bank.model.AccountType;
import com.bank.model.TransactionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class BankingIntegrationTest {
    private LoginController loginController;
    private AccountController accountController;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;

    @BeforeEach
    void setUp() {
        DatabaseManager.useJdbcUrl("jdbc:h2:mem:banktest_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        DatabaseManager.resetDatabase();
        customerDAO = new CustomerDAO();
        accountDAO = new AccountDAO(customerDAO);
        loginController = new LoginController(customerDAO);
        accountController = new AccountController(accountDAO);
    }

    @Test
    void completeLoginAccountAndTransactionFlowUsesTheDatabase() {
        assertTrue(loginController.handleLogin("user1", "pass1"));
        assertFalse(loginController.handleLogin("user1", "wrong-password"));
        List<AccountSummary> accounts = accountController.getAccounts("user1");
        assertEquals(1, accounts.size());
        AccountSummary savings = accounts.get(0);
        assertEquals(AccountType.SAVINGS, savings.type());
        assertEquals(1_000.00, accountController.getBalance("user1", savings.accountNumber()), 0.001);

        assertEquals(1_250.00, accountController.deposit("user1", savings.accountNumber(), 250), 0.001);
        assertEquals(-1, accountController.withdraw("user1", savings.accountNumber(), 50), 0.001);

        AccountSummary investment = accountController.openAccount("user1", "Investment", "Gaborone Main", 500, "");
        assertEquals(600.00, accountController.deposit("user1", investment.accountNumber(), 100), 0.001);
        assertEquals(500.00, accountController.withdraw("user1", investment.accountNumber(), 100), 0.001);

        YearMonth month = YearMonth.of(2026, 11);
        assertEquals(10, accountController.payMonthlyInterest(month));
        assertEquals(0, accountController.payMonthlyInterest(month));
        assertEquals(525.00, accountDAO.getBalance(investment.accountNumber()), 0.001);

        List<TransactionRecord> history = accountController.getTransactionHistory("user1", investment.accountNumber());
        assertEquals(List.of("INTEREST", "WITHDRAWAL", "DEPOSIT", "OPENING"), history.stream().map(TransactionRecord::type).toList());
        assertTrue(accountController.getTransactionHistory("user2", investment.accountNumber()).isEmpty());
    }

    @Test
    void registrationAndCustomerCrudPersistThroughJdbc() {
        assertTrue(loginController.registerIndividual("Ada", "Lovelace", "ada@example.test", "Gaborone",
                "ada", "safe-password"));
        assertTrue(loginController.handleLogin("ada", "safe-password"));
        assertEquals("Ada Lovelace", loginController.findCustomer("ada").fullName());
        assertTrue(customerDAO.updateContact("ada", "ada2@example.test", "Francistown"));
        assertEquals("Francistown", customerDAO.findByUsername("ada").address());
        assertTrue(customerDAO.delete("ada"));
        assertNull(customerDAO.findByUsername("ada"));
    }

    @Test
    void accountOpeningEnforcesOpeningRulesAndPersistsOwnership() {
        assertThrows(IllegalArgumentException.class, () -> accountController.openAccount(
                "user1", "Investment", "Gaborone Main", 499.99, ""));
        AccountSummary cheque = accountController.openAccount("user1", "Cheque", "Gaborone Main", 0,
                "Example employer, Gaborone");
        assertEquals(AccountType.CHEQUE, cheque.type());
        assertTrue(accountController.getAccounts("user1").stream()
                .anyMatch(account -> account.accountNumber().equals(cheque.accountNumber())));
        assertTrue(loginController.registerCompany("Example Ltd", "BW-EX-01", "example@example.test",
                "Gaborone", "example-company", "company-password"));
        assertThrows(IllegalArgumentException.class, () -> accountController.openAccount(
                "example-company", "Cheque", "Gaborone Main", 0, "Employer, Gaborone"));
    }

    @Test
    void databaseSeedsTenCustomersAndAccountsOnlyOnce() throws Exception {
        DatabaseManager.initializeDatabase();
        try (Connection connection = DatabaseManager.getConnection(); Statement statement = connection.createStatement()) {
            try (ResultSet customers = statement.executeQuery("SELECT COUNT(*) FROM customers")) {
                assertTrue(customers.next());
                assertEquals(10, customers.getInt(1));
            }
            try (ResultSet accounts = statement.executeQuery("SELECT COUNT(*) FROM accounts")) {
                assertTrue(accounts.next());
                assertEquals(10, accounts.getInt(1));
            }
        }
    }
}
