package com.bank;

import com.bank.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Initialize the H2 Database and insert the 10 sample records
        DatabaseManager.initializeDatabase(); 
        
        // 2. Load the GUI from the FXML file created in Scene Builder
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/view/login.fxml"));
        Parent root = loader.load();
        
        // 3. Set up the main window (Stage) and display it
        primaryStage.setTitle("Bank Login");
        primaryStage.setScene(new Scene(root, 350, 300));
        primaryStage.show();
    }
    
    public static void main(String[] args) { 
        launch(args); 
    }
}