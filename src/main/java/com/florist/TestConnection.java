package com.florist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de test simple pour vérifier la connexion MySQL.
 * Exécutez cette classe pour tester la connexion à la base de données.
 */
public class TestConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/florist_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mettez votre mot de passe MySQL ici si nécessaire

    /**
     * Méthode pour obtenir une connexion à la base de données.
     * 
     * @return Connection object ou null en cas d'erreur
     */
    public static Connection getConnection() {
        try {
            // Ouverture de connexion
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Connexion à MySQL réussie !");
            System.out.println("  - Base de données: florist_db");
            System.out.println("  - Utilisateur: " + USER);
            return connection;
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à MySQL:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Méthode main pour tester la connexion.
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Test de Connexion MySQL - Florist DB");
        System.out.println("========================================\n");

        Connection c = TestConnection.getConnection();

        if (c != null) {
            System.out.println("\n✓ Test réussi! La connexion fonctionne.");

            // Fermeture de la connexion
            try {
                c.close();
                System.out.println("✓ Connexion fermée proprement.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture: " + e.getMessage());
            }
        } else {
            System.out.println("\n✗ Test échoué! Impossible de se connecter.");
            System.out.println("\nVérifiez:");
            System.out.println("  1. MySQL (XAMPP) est démarré");
            System.out.println("  2. La base de données 'florist_db' existe");
            System.out.println("  3. Le mot de passe est correct (PASSWORD = \"\")");
        }

        System.out.println("\n========================================");
    }
}
