package com.bank;
import com.bank.controller.AccountController;
import com.bank.dao.AccountDAO;
import com.bank.dao.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    @BeforeAll public static void setup() { DatabaseManager.initializeDatabase(); }

    @Test
    public void testFullDepositFlow() {
        AccountController controller = new AccountController();
        double newBalance = controller.handleDeposit("ACC001", 500.0);
        assertEquals(1500.0, newBalance, "Balance should be updated in memory");
        AccountDAO dao = new AccountDAO();
        assertEquals(1500.0, dao.getBalance("ACC001"), "Database should reflect new balance");
    }
}