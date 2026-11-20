package com.bank.view;

import com.bank.controller.AccountController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DashboardFxmlController {

    @FXML private TextField accNumField;
    @FXML private TextField amountField;
    @FXML private Label resultLabel;

    private AccountController logicController = new AccountController();

    @FXML
    public void handleDeposit() {
        try {
            String accNum = accNumField.getText();
            double amount = Double.parseDouble(amountField.getText());
            
            double newBalance = logicController.handleDeposit(accNum, amount);
            
            if (newBalance >= 0) {
                resultLabel.setText("Success! New Balance: BWP " + newBalance);
                resultLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                resultLabel.setText("Transaction Failed.");
                resultLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid amount.");
            resultLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}