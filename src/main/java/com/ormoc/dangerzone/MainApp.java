package com.ormoc.dangerzone;

import com.ormoc.dangerzone.config.DatabaseConfig;
import com.ormoc.dangerzone.server.WebServer;
import com.ormoc.dangerzone.ui.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Main Application Entry Point
 * Ormoc Danger Zone Mapping and Historical Information System
 */
public class MainApp extends Application {
    
    private WebServer webServer;
    private MainWindow mainWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            if (!dbConfig.testConnection()) {
                showErrorAlert("Database Connection Failed",
                    "Cannot connect to MySQL database.\n\n" +
                    "Please check:\n" +
                    "1. MySQL is running\n" +
                    "2. Database 'dangerzone_ormoc' exists\n" +
                    "3. Username/password in application.properties is correct");
                Platform.exit();
                return;
            }

            System.out.println("✓ Database connection successful");

            // Start embedded web server
            webServer = new WebServer();
            webServer.start();
            
            System.out.println("✓ Web server started on http://" + 
                dbConfig.getServerHost() + ":" + dbConfig.getServerPort());

            // Create and show main window
            mainWindow = new MainWindow(primaryStage);
            mainWindow.show();

            System.out.println("✓ Application started successfully");

            // Set close handler
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                handleExit();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Application Error", 
                "Failed to start application: " + e.getMessage());
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        // Cleanup on application exit
        if (webServer != null) {
            try {
                webServer.stop();
                System.out.println("✓ Web server stopped");
            } catch (Exception e) {
                System.err.println("Error stopping web server: " + e.getMessage());
            }
        }
    }

    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("The application and web server will be closed.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}