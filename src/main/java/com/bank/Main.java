package com.bank;

import com.bank.dao.DatabaseManager;
import com.bank.service.InterestScheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
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
    
    public static void main(String[] args) { 
        launch(args); 
    }
}
