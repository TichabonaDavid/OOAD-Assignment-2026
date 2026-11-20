package com.bank.view;

import com.bank.controller.AccountController;
import com.bank.controller.LoginController;
import com.bank.model.AccountSummary;
import com.bank.model.TransactionRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DashboardFxmlController {
    @FXML private Label customerLabel;
    @FXML private ComboBox<AccountSummary> accountCombo;
    @FXML private Label balanceLabel;
    @FXML private TextField amountField;
    @FXML private Label resultLabel;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField branchField;
    @FXML private TextField openingBalanceField;
    @FXML private TextField employmentDetailsField;
    @FXML private Label investmentRuleLabel;
    @FXML private TableView<TransactionRecord> historyTable;
    @FXML private TableColumn<TransactionRecord, String> dateColumn;
    @FXML private TableColumn<TransactionRecord, String> typeColumn;
    @FXML private TableColumn<TransactionRecord, String> amountColumn;
    @FXML private TableColumn<TransactionRecord, String> balanceAfterColumn;

    private final AccountController accountController = new AccountController();
    private final LoginController loginController = new LoginController();
    private String username;

    @FXML
    private void initialize() {
        accountTypeCombo.setItems(FXCollections.observableArrayList("Savings", "Investment", "Cheque"));
        accountTypeCombo.getSelectionModel().selectFirst();
        accountCombo.valueProperty().addListener((observable, previous, selected) -> displaySelectedAccount());
        accountTypeCombo.valueProperty().addListener((observable, previous, selected) -> updateOpeningFields());
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().createdAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().type()));
        amountColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.format("BWP %,.2f", cell.getValue().amount())));
        balanceAfterColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.format("BWP %,.2f", cell.getValue().balanceAfter())));
        updateOpeningFields();
    }

    public void setUsername(String username) {
        this.username = username;
        var customer = loginController.findCustomer(username);
        customerLabel.setText(customer == null ? username : customer.fullName());
        reloadAccounts(null);
    }

    @FXML
    public void handleRefreshBalance() { displaySelectedAccount(); }

    @FXML
    public void handleDeposit() { performAmountAction(true); }

    @FXML
    public void handleWithdrawal() { performAmountAction(false); }

    @FXML
    public void handleOpenAccount() {
        try {
            double openingBalance = Double.parseDouble(openingBalanceField.getText().trim());
            AccountSummary opened = accountController.openAccount(username, accountTypeCombo.getValue(),
                    branchField.getText(), openingBalance, employmentDetailsField.getText());
            resultLabel.setStyle("-fx-text-fill: #16794a;");
            resultLabel.setText("Account opened: " + opened.accountNumber());
            reloadAccounts(opened.accountNumber());
            openingBalanceField.clear();
        } catch (NumberFormatException ex) {
            showError("Enter a valid opening balance.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (RuntimeException ex) {
            showError("Could not open the account. Please try again.");
        }
    }

    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/bank/view/login.fxml"));
            Stage stage = (Stage) customerLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 560, 540));
            stage.setTitle("Morula Bank");
        } catch (IOException ex) {
            showError("Could not return to the sign-in screen.");
        }
    }

    private void performAmountAction(boolean deposit) {
        AccountSummary account = accountCombo.getValue();
        if (account == null) { showError("Select an account first."); return; }
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            double balance = deposit
                    ? accountController.deposit(username, account.accountNumber(), amount)
                    : accountController.withdraw(username, account.accountNumber(), amount);
            if (balance < 0) {
                showError(deposit ? "Deposit failed. Check the amount and account." :
                        "Withdrawal is not allowed for this account or the balance is insufficient.");
                return;
            }
            balanceLabel.setText(String.format("BWP %,.2f", balance));
            resultLabel.setStyle("-fx-text-fill: #16794a;");
            resultLabel.setText(deposit ? "Deposit recorded." : "Withdrawal recorded.");
            amountField.clear();
            reloadAccounts(account.accountNumber());
        } catch (NumberFormatException ex) {
            showError("Enter a valid amount in BWP.");
        } catch (RuntimeException ex) {
            showError("The transaction could not be completed. Please try again.");
        }
    }

    private void reloadAccounts(String selectedAccountNumber) {
        var accounts = accountController.getAccounts(username);
        accountCombo.setItems(FXCollections.observableArrayList(accounts));
        AccountSummary selected = accounts.stream()
                .filter(account -> account.accountNumber().equals(selectedAccountNumber))
                .findFirst().orElse(accounts.isEmpty() ? null : accounts.get(0));
        accountCombo.getSelectionModel().select(selected);
        if (selected == null) {
            balanceLabel.setText("BWP --");
            historyTable.getItems().clear();
        }
    }

    private void displaySelectedAccount() {
        AccountSummary selected = accountCombo.getValue();
        if (selected == null || username == null) return;
        double balance = accountController.getBalance(username, selected.accountNumber());
        balanceLabel.setText(balance < 0 ? "BWP --" : String.format("BWP %,.2f", balance));
        historyTable.setItems(FXCollections.observableArrayList(
                accountController.getTransactionHistory(username, selected.accountNumber())));
    }

    private void updateOpeningFields() {
        boolean cheque = "Cheque".equals(accountTypeCombo.getValue());
        employmentDetailsField.setDisable(!cheque);
        employmentDetailsField.setPromptText(cheque ? "Employer and work address" : "Only required for cheque accounts");
        investmentRuleLabel.setText("Investment accounts require an opening balance of at least BWP 500.00.");
    }

    private void showError(String message) {
        resultLabel.setStyle("-fx-text-fill: #b42318;");
        resultLabel.setText(message);
    }
}
