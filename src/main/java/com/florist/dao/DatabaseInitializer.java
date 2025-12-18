package com.florist.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility class to initialize the SQLite database from schema.sql
 */
public class DatabaseInitializer {

    public static void initializeDatabase() {
        try {
            System.out.println("Initializing database from schema.sql...");

            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Read schema.sql from resources
            InputStream is = DatabaseInitializer.class.getResourceAsStream("/schema.sql");
            if (is == null) {
                System.err.println("Could not find schema.sql in resources!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sql.append(line).append("\n");
            }

            // Split by semicolon and execute each statement
            String[] statements = sql.toString().split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }

            reader.close();
            stmt.close();

            System.out.println("✓ Database initialized successfully!");

        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
