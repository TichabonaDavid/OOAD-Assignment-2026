package com.bank.dao;

import com.bank.model.AccountSummary;
import com.bank.model.AccountType;
import com.bank.model.ChequeAccount;
import com.bank.model.InvestmentAccount;
import com.bank.model.SavingsAccount;
import com.bank.model.TransactionRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountDAO {
    private final CustomerDAO customerDAO;

    public AccountDAO() { this(new CustomerDAO()); }
    public AccountDAO(CustomerDAO customerDAO) { this.customerDAO = customerDAO; }

    public AccountSummary createAccount(String username, AccountType type, String branch,
                                        double openingBalance, String employmentDetails) {
        if (type == null) throw new IllegalArgumentException("Account type is required.");
        long customerId = customerDAO.getCustomerId(username);
        if (customerId < 0) throw new IllegalArgumentException("Customer was not found.");
        if (type == AccountType.CHEQUE) {
            var customer = customerDAO.findByUsername(username);
            if (customer == null || !"INDIVIDUAL".equals(customer.customerType())) {
                throw new IllegalArgumentException("Cheque accounts are available to individual customers only.");
            }
        }

        String accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        switch (type) {
            case SAVINGS -> new SavingsAccount(accountNumber, branch, openingBalance);
            case INVESTMENT -> new InvestmentAccount(accountNumber, branch, openingBalance);
            case CHEQUE -> new ChequeAccount(accountNumber, branch, openingBalance, employmentDetails);
        }
        BigDecimal opening = money(openingBalance);
        String sql = "INSERT INTO accounts (account_number,customer_id,account_type,branch,balance,employment_details) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            statement.setLong(2, customerId);
            statement.setString(3, type.name());
            statement.setString(4, branch.trim());
            statement.setBigDecimal(5, opening);
            statement.setString(6, type == AccountType.CHEQUE ? employmentDetails.trim() : null);
            statement.executeUpdate();
            if (opening.signum() > 0) recordTransaction(connection, accountNumber, "OPENING", opening, opening, "Opening deposit");
                connection.commit();
            return new AccountSummary(accountNumber, type, branch.trim(), opening.doubleValue());
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not open the account.", ex);
        }
    }

    public List<AccountSummary> listForCustomer(String username) {
        String sql = "SELECT a.account_number,a.account_type,a.branch,a.balance FROM accounts a "
                + "JOIN customers c ON c.id=a.customer_id WHERE c.username=? ORDER BY a.opened_at,a.account_number";
        List<AccountSummary> accounts = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) accounts.add(toSummary(rows));
            }
            return accounts;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load customer accounts.", ex);
        }
    }

    public AccountSummary find(String accountNumber) {
        String sql = "SELECT account_number,account_type,branch,balance FROM accounts WHERE account_number=?";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rows = statement.executeQuery()) { return rows.next() ? toSummary(rows) : null; }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not find the account.", ex);
        }
    }

    public boolean updateBranch(String accountNumber, String branch) {
        if (branch == null || branch.isBlank()) throw new IllegalArgumentException("Branch is required.");
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET branch=? WHERE account_number=?")) {
            statement.setString(1, branch.trim());
            statement.setString(2, accountNumber);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not update the account.", ex);
        }
    }

    public boolean delete(String accountNumber) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM accounts WHERE account_number=?")) {
            statement.setString(1, accountNumber);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not delete the account.", ex);
        }
    }

    public double getBalance(String accountNumber) {
        AccountSummary account = find(accountNumber);
        return account == null ? -1 : account.balance();
    }

    public double deposit(String accountNumber, double amount) {
        return applyTransaction(accountNumber, amount, "DEPOSIT", false, "Customer deposit");
    }

    public double withdraw(String accountNumber, double amount) {
        return applyTransaction(accountNumber, amount, "WITHDRAWAL", true, "Customer withdrawal");
    }

    public double creditMonthlyInterest(String accountNumber) {
        AccountSummary account = find(accountNumber);
        if (account == null || account.type() == AccountType.CHEQUE) return -1;
        YearMonth month = YearMonth.now();
        return creditInterestForAccount(accountNumber, month);
    }

    public int payMonthlyInterest(YearMonth month) {
        if (month == null) throw new IllegalArgumentException("Payment month is required.");
        int payments = 0;
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            String accountsSql = "SELECT account_number,account_type,balance FROM accounts WHERE account_type IN ('SAVINGS','INVESTMENT') FOR UPDATE";
            try (PreparedStatement accounts = connection.prepareStatement(accountsSql); ResultSet rows = accounts.executeQuery()) {
                while (rows.next()) {
                    String accountNumber = rows.getString("account_number");
                    AccountType type = AccountType.valueOf(rows.getString("account_type"));
                    BigDecimal balance = rows.getBigDecimal("balance");
                    Savepoint savepoint = connection.setSavepoint();
                    try (PreparedStatement claim = connection.prepareStatement(
                            "INSERT INTO interest_payments (account_number,payment_month) VALUES (?,?)")) {
                        claim.setString(1, accountNumber);
                        claim.setDate(2, Date.valueOf(month.atDay(1)));
                        claim.executeUpdate();
                    } catch (SQLException duplicate) {
                        if (duplicate.getSQLState() != null && duplicate.getSQLState().startsWith("23")) {
                            connection.rollback(savepoint);
                            continue;
                        }
                        throw duplicate;
                    }
                    BigDecimal interest = balance.multiply(BigDecimal.valueOf(type.monthlyRate())).setScale(2, RoundingMode.HALF_UP);
                    if (interest.signum() > 0) {
                        BigDecimal newBalance = balance.add(interest);
                        updateBalance(connection, accountNumber, newBalance);
                        recordTransaction(connection, accountNumber, "INTEREST", interest, newBalance,
                                "Monthly interest for " + month);
                        payments++;
                    }
                    connection.releaseSavepoint(savepoint);
                }
            }
            connection.commit();
            return payments;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not pay monthly interest.", ex);
        }
    }

    public List<TransactionRecord> transactionHistory(String accountNumber) {
        String sql = "SELECT id,account_number,transaction_type,amount,balance_after,created_at,description "
                + "FROM transactions WHERE account_number=? ORDER BY id DESC";
        List<TransactionRecord> history = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    history.add(new TransactionRecord(rows.getLong("id"), rows.getString("account_number"),
                            rows.getString("transaction_type"), rows.getBigDecimal("amount").doubleValue(),
                            rows.getBigDecimal("balance_after").doubleValue(),
                            rows.getTimestamp("created_at").toLocalDateTime(), rows.getString("description")));
                }
            }
            return history;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load transaction history.", ex);
        }
    }

    private double creditInterestForAccount(String accountNumber, YearMonth month) {
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement find = connection.prepareStatement(
                    "SELECT account_type,balance FROM accounts WHERE account_number=? FOR UPDATE")) {
                find.setString(1, accountNumber);
                try (ResultSet row = find.executeQuery()) {
                    if (!row.next()) { connection.rollback(); return -1; }
                    AccountType type = AccountType.valueOf(row.getString("account_type"));
                    if (type == AccountType.CHEQUE) { connection.rollback(); return -1; }
                    try (PreparedStatement claim = connection.prepareStatement(
                            "INSERT INTO interest_payments(account_number,payment_month) VALUES (?,?)")) {
                        claim.setString(1, accountNumber);
                        claim.setDate(2, Date.valueOf(month.atDay(1)));
                        claim.executeUpdate();
                    }
                    BigDecimal current = row.getBigDecimal("balance");
                    BigDecimal interest = current.multiply(BigDecimal.valueOf(type.monthlyRate())).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal next = current.add(interest);
                    if (interest.signum() > 0) {
                        updateBalance(connection, accountNumber, next);
                        recordTransaction(connection, accountNumber, "INTEREST", interest, next, "Monthly interest for " + month);
                    }
                    connection.commit();
                    return next.doubleValue();
                }
            } catch (SQLException ex) { connection.rollback(); throw ex; }
        } catch (SQLException ex) {
            if (ex.getSQLState() != null && ex.getSQLState().startsWith("23")) return -1;
            throw new IllegalStateException("Could not credit account interest.", ex);
        }
    }

    private double applyTransaction(String accountNumber, double amount, String type, boolean withdrawal, String description) {
        if (accountNumber == null || accountNumber.isBlank() || !Double.isFinite(amount) || amount <= 0) return -1;
        BigDecimal transactionAmount = money(amount);
        if (transactionAmount.signum() <= 0) return -1;
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement find = connection.prepareStatement(
                    "SELECT account_type,balance FROM accounts WHERE account_number=? FOR UPDATE")) {
                find.setString(1, accountNumber.trim());
                try (ResultSet row = find.executeQuery()) {
                    if (!row.next()) { connection.rollback(); return -1; }
                    AccountType accountType = AccountType.valueOf(row.getString("account_type"));
                    BigDecimal current = row.getBigDecimal("balance");
                    if (withdrawal && (!accountType.withdrawalsAllowed() || transactionAmount.compareTo(current) > 0)) {
                        connection.rollback();
                        return -1;
                    }
                    BigDecimal next = withdrawal ? current.subtract(transactionAmount) : current.add(transactionAmount);
                    updateBalance(connection, accountNumber.trim(), next);
                    recordTransaction(connection, accountNumber.trim(), type, transactionAmount, next, description);
                    connection.commit();
                    return next.doubleValue();
                }
            } catch (SQLException ex) { connection.rollback(); throw ex; }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not record the " + type.toLowerCase() + ".", ex);
        }
    }

    private void updateBalance(Connection connection, String accountNumber, BigDecimal balance) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement("UPDATE accounts SET balance=? WHERE account_number=?")) {
            update.setBigDecimal(1, balance);
            update.setString(2, accountNumber);
            update.executeUpdate();
        }
    }

    private void recordTransaction(Connection connection, String accountNumber, String type,
                                   BigDecimal amount, BigDecimal balanceAfter, String description) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO transactions(account_number,transaction_type,amount,balance_after,description) VALUES (?,?,?,?,?)")) {
            insert.setString(1, accountNumber);
            insert.setString(2, type);
            insert.setBigDecimal(3, amount);
            insert.setBigDecimal(4, balanceAfter);
            insert.setString(5, description);
            insert.executeUpdate();
        }
    }

    private static BigDecimal money(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Amount must be a valid non-negative number.");
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private static AccountSummary toSummary(ResultSet row) throws SQLException {
        return new AccountSummary(row.getString("account_number"), AccountType.valueOf(row.getString("account_type")),
                row.getString("branch"), row.getBigDecimal("balance").doubleValue());
    }
}

