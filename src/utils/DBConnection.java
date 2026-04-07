package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = envOrDefault("DB_URL", "jdbc:mysql://localhost:3306/online_exam");
    private static final String DB_USER = envOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = envOrDefault("DB_PASSWORD", "root");

    public static Connection getConnection() {
        Connection conn = tryConnect(DB_USER, DB_PASSWORD);
        if (conn != null) {
            return conn;
        }

        if (System.getenv("DB_USER") == null && System.getenv("DB_PASSWORD") == null) {
            conn = tryConnect("root", "");
            if (conn != null) {
                return conn;
            }

            conn = tryConnect("root", "root");
            if (conn != null) {
                return conn;
            }
        }

        return null;
    }

    private static Connection tryConnect(String user, String password) {
        try {
            return DriverManager.getConnection(URL, user, password);
        } catch (Exception e) {
            System.err.println("Database connection failed for user '" + user + "' on URL '" + URL + "': " + e.getMessage());
            return null;
        }
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
