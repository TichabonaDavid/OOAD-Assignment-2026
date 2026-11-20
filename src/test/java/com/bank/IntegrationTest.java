package com.bank;
<<<<<<< HEAD

=======
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
import com.bank.controller.AccountController;
import com.bank.dao.AccountDAO;
import com.bank.dao.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {
    @BeforeAll
    static void setup() {
        DatabaseManager.useJdbcUrl("jdbc:h2:mem:deposit_test;DB_CLOSE_DELAY=-1");
        DatabaseManager.resetDatabase();
    }

    @Test
    void testFullDepositFlow() {
        AccountController controller = new AccountController();
        double newBalance = controller.deposit("user1", "ACC001", 500.0);
        assertEquals(1500.0, newBalance, "Balance should be updated");
        assertEquals(1500.0, new AccountDAO().getBalance("ACC001"),
                "Database should reflect new balance");
    }
}
=======
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
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
