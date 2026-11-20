package com.bank;

import com.bank.dao.DatabaseManager;
<<<<<<< HEAD
import com.bank.service.InterestScheduler;
=======
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
<<<<<<< HEAD
    private InterestScheduler interestScheduler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.initializeDatabase();
        interestScheduler = new InterestScheduler(new com.bank.dao.AccountDAO());
        interestScheduler.start();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/view/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Morula Bank");
        primaryStage.setScene(new Scene(root, 560, 540));
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (interestScheduler != null) interestScheduler.close();
    }
=======

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
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
    
    public static void main(String[] args) { 
        launch(args); 
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
