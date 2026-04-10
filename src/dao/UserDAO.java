package dao;

import model.User;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private String lastRegisterError = "";

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users(name,email,password,role) VALUES(?,?,?,?)";
        lastRegisterError = "";

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            lastRegisterError = "Could not connect to the database. Check DB_URL, DB_USER, DB_PASSWORD and ensure MySQL is running.";
            return false;
        }

        try (Connection ignored = conn;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                lastRegisterError = "This email is already registered.";
            } else if (e.getErrorCode() == 1146) {
                lastRegisterError = "Required table 'users' was not found. Create database tables before running.";
            } else {
                lastRegisterError = "Database error while registering: " + e.getMessage();
            }
            e.printStackTrace();
        } catch (Exception e) {
            lastRegisterError = "Unexpected error while registering: " + e.getMessage();
            e.printStackTrace();
        }

        return false;
    }

    public String getLastRegisterError() {
        return lastRegisterError;
    }

    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
