package com.example.projet.filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:derby:monDB;create=true;";  // URL de la base de données
    private static final String DB_USER = "user";  // Nom d'utilisateur (si nécessaire)
    private static final String DB_PASSWORD = "password";  // Mot de passe (si nécessaire)

    static {
        try {
            // Charger le driver Derby
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Méthode pour fermer la connexion (si nécessaire)
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour vérifier si la table existe déjà, sinon la créer
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Vérifier si la table existe déjà
            DatabaseMetaData metaData = conn.getMetaData();
            var resultSet = metaData.getTables(null, null, "IMAGES_TAGS", null);

            // Si la table n'existe pas, on la crée
            if (!resultSet.next()) {
                String createTableQuery = "CREATE TABLE APP.images_tags  IF NOT EXISTS ("
                        + "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                        + "image_name VARCHAR(255), "
                        + "image_url VARCHAR(255), "
                        + "tag VARCHAR(255), "
                        + "transformations VARCHAR(1024), "
                        + "CONSTRAINT unique_tag UNIQUE (tag) "
                        + ")";


                // Créer la table
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(createTableQuery);
                    System.out.println("Table 'images_tags' créée avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("La table 'images_tags' existe déjà.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
