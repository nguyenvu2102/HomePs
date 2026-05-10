package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("all")
public class DBConnection {
    private static final String URL = java.util.Objects.requireNonNull(resolveUrl());
    private static final String USER = java.util.Objects.requireNonNull(resolveUser());
    private static final String PASS = java.util.Objects.requireNonNull(resolvePassword());

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Cannot connect to HomePS database. Check DB_URL/HOMEPS_DB_URL, DB_USER/HOMEPS_DB_USER and DB_PASSWORD/HOMEPS_DB_PASS.", e);
        }
    }

    private static String resolveUrl() {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl != null && !dbUrl.isBlank()) {
            return dbUrl;
        }

        String homepsUrl = System.getenv("HOMEPS_DB_URL");
        if (homepsUrl != null && !homepsUrl.isBlank()) {
            return homepsUrl;
        }

        return "jdbc:postgresql://localhost:5432/homeps";
    }

    private static String resolveUser() {
        String value = System.getenv("DB_USER");
        if (isBlank(value)) {
            value = System.getenv("HOMEPS_DB_USER");
        }
        if (isBlank(value)) {
            value = "postgres";
        }
        return value;
    }

    private static String resolvePassword() {
        String value = System.getenv("DB_PASSWORD");
        if (isBlank(value)) {
            value = System.getenv("HOMEPS_DB_PASS");
        }
        if (isBlank(value)) {
            value = System.getenv("HOMEPS_DB_PASSWORD");
        }
        if (isBlank(value)) {
            value = "postgres";
        }
        return value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}