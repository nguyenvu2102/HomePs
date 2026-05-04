package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://db:5432/homeps");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String PASS = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Cannot connect to HomePS database. Check DB_URL/DB_USER/DB_PASSWORD.", e);
        }
    }
}