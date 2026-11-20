package com.bank.view;

import com.bank.controller.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFxmlController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final LoginController loginController = new LoginController();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        try {
            if (!loginController.handleLogin(username, password)) {
                errorLabel.setText("We could not sign you in. Check your username and password.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/view/dashboard.fxml"));
            Parent root = loader.load();
            DashboardFxmlController dashboard = loader.getController();
            dashboard.setUsername(username);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 980, 700));
            stage.setTitle("Morula Bank - Accounts");
        } catch (IOException | RuntimeException ex) {
            errorLabel.setText("Sign in is temporarily unavailable. Please try again.");
        }
    }

    @FXML
    public void handleOpenRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/view/register.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Create a Morula Bank profile");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(usernameField.getScene().getWindow());
            dialog.setScene(new Scene(root, 560, 680));
            dialog.showAndWait();
        } catch (IOException ex) {
            errorLabel.setText("Registration is temporarily unavailable.");
        }
    }
}
