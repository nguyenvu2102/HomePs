package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/HomePS";
    private static final String USER = "postgres";
    private static final String PASS = "mat_khau_cua_ban";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Lỗi kết nối Database!");
            e.printStackTrace();
            return null;
        }
    }
}