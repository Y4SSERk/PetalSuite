package com.florist;

import com.florist.dao.DatabaseConnection;
import com.florist.service.InventoryService;
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

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            System.out.println("========================================");
            System.out.println("Florist Management System Starting...");
            System.out.println("========================================");

            if (!DatabaseConnection.testConnection()) {
                System.err.println("ERREUR: Impossible de se connecter à la base de données!");
                System.err.println("Vérifiez que les fichiers sont accessibles.");
                showDatabaseError(primaryStage);
                return;
            }

            // Initialize database (create tables from schema.sql)
            System.out.println("\n--- Initialisation de la base de données ---");
            com.florist.dao.DatabaseInitializer.initializeDatabase();
            System.out.println("-----------------------------------------------\n");

            // Run automatic inventory checks on startup
            System.out.println("\n--- Vérification automatique de l'inventaire ---");
            InventoryService inventoryService = new InventoryService();
            int alertsGenerated = inventoryService.checkAllAlerts();
            System.out.println("Vérification terminée. " + alertsGenerated + " alerte(s) générée(s).");
            System.out.println("-----------------------------------------------\n");

            // Load main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            primaryStage.setTitle("Florist Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setOnCloseRequest(event -> {
                DatabaseConnection.closeConnection();
                System.out.println("Application fermée.");
            });

            primaryStage.show();
            System.out.println("✓ Application lancée avec succès!");

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'application:");
            e.printStackTrace();
        }
    }

    /**
     * Shows a database error dialog.
     */
    private void showDatabaseError(Stage stage) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText("Impossible de se connecter à la base de données");
        alert.setContentText(
                "Veuillez vérifier:\n" +
                        "1. MySQL est démarré\n" +
                        "2. La base de données 'florist_db' existe\n" +
                        "3. Les identifiants dans DatabaseConnection.java sont corrects\n" +
                        "4. Le script schema.sql a été exécuté\n\n" +
                        "Commande: mysql -u root -p florist_db < src/main/resources/schema.sql");
        alert.showAndWait();
        System.exit(1);
    }

    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
