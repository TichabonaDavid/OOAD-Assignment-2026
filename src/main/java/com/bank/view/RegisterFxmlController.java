package com.bank.view;

import com.bank.controller.LoginController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterFxmlController {
    @FXML private ComboBox<String> customerTypeCombo;
    @FXML private Label firstNameLabel;
    @FXML private Label surnameLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField surnameField;
    @FXML private Label companyNameLabel;
    @FXML private Label companyRegNoLabel;
    @FXML private TextField companyNameField;
    @FXML private TextField companyRegNoField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label resultLabel;

    private final LoginController loginController = new LoginController();

    @FXML
    private void initialize() {
        customerTypeCombo.getItems().setAll("Individual", "Company");
        customerTypeCombo.getSelectionModel().selectFirst();
        customerTypeCombo.valueProperty().addListener((observable, previous, current) -> updateCustomerFields());
        updateCustomerFields();
    }

    @FXML
    public void handleRegister() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("The passwords do not match.");
            return;
        }
        try {
            boolean created = "Company".equals(customerTypeCombo.getValue())
                    ? loginController.registerCompany(companyNameField.getText(), companyRegNoField.getText(),
                        emailField.getText(), addressField.getText(), usernameField.getText(), passwordField.getText())
                    : loginController.registerIndividual(firstNameField.getText(), surnameField.getText(),
                        emailField.getText(), addressField.getText(), usernameField.getText(), passwordField.getText());
            if (!created) {
                showError("That username is already registered.");
                return;
            }
            resultLabel.setStyle("-fx-text-fill: #16794a; -fx-font-size: 12px;");
            resultLabel.setText("Profile created. You can now sign in.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (RuntimeException ex) {
            showError("Could not create the profile. Check the details and try again.");
        }
    }

    @FXML
    public void handleClose() {
        ((Stage) resultLabel.getScene().getWindow()).close();
    }

    private void showError(String message) {
        resultLabel.setStyle("-fx-text-fill: #b42318; -fx-font-size: 12px;");
        resultLabel.setText(message);
    }

    private void updateCustomerFields() {
        boolean company = "Company".equals(customerTypeCombo.getValue());
        firstNameLabel.setVisible(!company); firstNameLabel.setManaged(!company);
        surnameLabel.setVisible(!company); surnameLabel.setManaged(!company);
        firstNameField.setVisible(!company); firstNameField.setManaged(!company);
        surnameField.setVisible(!company); surnameField.setManaged(!company);
        companyNameLabel.setVisible(company); companyNameLabel.setManaged(company);
        companyRegNoLabel.setVisible(company); companyRegNoLabel.setManaged(company);
        companyNameField.setVisible(company); companyNameField.setManaged(company);
        companyRegNoField.setVisible(company); companyRegNoField.setManaged(company);
    }
}
