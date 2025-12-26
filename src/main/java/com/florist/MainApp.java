package com.florist;

import com.florist.infrastructure.persistence.DatabaseConnection;
import com.florist.infrastructure.persistence.DatabaseInitializer;
import com.florist.config.ServiceFactory;
import com.florist.threads.BackgroundTaskManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Florist Management System.
 * Entry point for the JavaFX application.
 */
public class MainApp extends Application {

    private BackgroundTaskManager backgroundTaskManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Attempt to establish database connection
            try {
                DatabaseConnection.getConnection();
            } catch (Exception e) {
                showDatabaseError(primaryStage);
                return;
            }

            // Initialize database schema
            DatabaseInitializer.initializeDatabase();

            // Run database migration for severity column
            runDatabaseMigration();

            // Run inventory checks
            ServiceFactory.getInstance().getInventoryService().checkAllAlerts();

            // Start background tasks (Phase 6: Threads & Concurrence)
            backgroundTaskManager = new BackgroundTaskManager();
            backgroundTaskManager.startAutoBackup();
            backgroundTaskManager.startAlertMonitoring();

            // Load main UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            primaryStage.setTitle("Florist Management System");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Immersive Full Screen
            primaryStage.setFullScreenExitHint(""); // Hide the "Press ESC" overlay
            primaryStage.setOnCloseRequest(event -> {
                shutdown();
            });

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a database error dialog.
     */
    private void showDatabaseError(Stage stage) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("Unable to connect to the database");
        alert.setContentText(
                "Please check:\n" +
                        "1. MySQL is running\n" +
                        "2. Database 'florist_db' exists\n" +
                        "3. Credentials in DatabaseConnection.java are correct\n" +
                        "4. Schema.sql script has been executed\n\n" +
                        "Command: mysql -u root -p florist_db < src/main/resources/schema.sql");
        alert.showAndWait();
        System.exit(1);
    }

    /**
     * Runs database migration to add severity column if it doesn't exist.
     */
    private void runDatabaseMigration() {
        try {
            java.sql.Connection conn = DatabaseConnection.getConnection();
            java.sql.Statement stmt = conn.createStatement();

            String sql = "ALTER TABLE stock_alerts " +
                    "ADD COLUMN IF NOT EXISTS severity VARCHAR(10) DEFAULT 'WARNING' " +
                    "COMMENT 'DANGER or WARNING' AFTER alert_type";

            stmt.executeUpdate(sql);
            System.out.println("✓ Database migration: severity column added/verified");

            stmt.close();
        } catch (Exception e) {
            System.err.println("⚠ Database migration warning: " + e.getMessage());
            // Don't fail the app if migration fails - column might already exist
        }
    }

    /**
     * Gracefully shutdown the application.
     */
    private void shutdown() {
        if (backgroundTaskManager != null) {
            backgroundTaskManager.shutdown();
        }
        DatabaseConnection.closeConnection();
    }

    @Override
    public void stop() {
        shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
