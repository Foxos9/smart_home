package smart_home.database;

import java.sql.*;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import smart_home.utils.LoginStatus;
import smart_home.utils.User;

public class Database {
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    // Establish a connection to the database (static to be used across methods)
    public static void connect(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // Query the database to validate login credentials
    public static LoginStatus validateLogin(String username, String password) {
        String query = "SELECT password_hash, role FROM Users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // If user exists
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");

                // Verify the password
                if (BCrypt.verifyer().verify(password.toCharArray(), storedHash).verified) {
                    // Check role and return corresponding status
                    if ("admin".equals(role)) {
                        return LoginStatus.SUCCESS_ADMIN;
                    } else {
                        return LoginStatus.SUCCESS_USER;
                    }
                } else {
                    return LoginStatus.FAILED_INVALID_CREDENTIALS; // Invalid password
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return LoginStatus.ERROR; // Database error
        }
        return LoginStatus.FAILED_INVALID_CREDENTIALS; // Username not found
    }

    // Query the database to get user information by username
    public static User getUserInfo(String username) {
        String query = "SELECT user_id, username, email, role FROM Users WHERE username = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // If user exists
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean isAdmin = "admin".equals(role); // Check if the role is admin
                return new User(userId, username, email, isAdmin);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // Return null if user is not found
    }

    // Query the database to get all users
    public static List<User> getUsers() {
        String query = "SELECT user_id, username, email, role FROM Users WHERE username LIKE '%'";
        List<User> users = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Iterate through the ResultSet and populate the List
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean isAdmin = "admin".equals(role); // Check if the role is admin
                users.add(new User(userId, username, email, isAdmin));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return users;
    }

    // Insert Device State data
    public static void insertDeviceState(int deviceId, String state) {
        String query = "INSERT INTO DeviceStates (device_id, state) VALUES (?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, deviceId);
            stmt.setString(2, state); // Assuming 'state' is a JSON string
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Insert log for actions performed
    public static void insertLog(int userId, int deviceId, String action) {
        String query = "INSERT INTO Logs (user_id, device_id, action) VALUES (?, ?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, deviceId);
            stmt.setString(3, action);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Insert Security Alert data
    public static void insertSecurityAlert(int deviceId, String alertType) {
        String query = "INSERT INTO SecurityAlerts (device_id, alert_type) VALUES (?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, deviceId);
            stmt.setString(2, alertType);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Update device status
    public static void updateDeviceStatus(int deviceId, String status) {
        String query = "UPDATE Devices SET status = ? WHERE device_id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, deviceId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to get the latest device state for a given device_id
    public static String getLatestDeviceState(int deviceId) {
        String state = null;
        String query = "SELECT state FROM DeviceStates WHERE device_id = ? ORDER BY logged_at DESC LIMIT 1";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, deviceId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                state = rs.getString("state");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return state;
    }

    // Method to get the latest value of a sensor from the DeviceStates table
    public static double getLatestSensorValue(int deviceId, String key) {
        String stateJson = getLatestDeviceState(deviceId);
        if (stateJson != null) {
            try {
                JSONObject state = new JSONObject(stateJson);
                return state.optDouble(key, 0); // Default to 0 if key not found
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0; // Return 0 if no state or key not found
    }

    // Method to close the connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
