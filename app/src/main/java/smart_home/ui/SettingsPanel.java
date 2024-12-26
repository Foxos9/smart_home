package smart_home.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import java.sql.*;

import smart_home.database.Database;
import smart_home.utils.LoginListener;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class SettingsPanel extends JPanel implements LoginListener {
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTabbedPane tabbedPane;

    public SettingsPanel() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void onLogin(String username, boolean isAdmin) {
        if (isAdmin) {
            // User Management Panel
            JPanel userSettings = new JPanel(new BorderLayout());
            setupUserManagementPanel(userSettings);
            tabbedPane.add("Users", userSettings);
        } else {
            // Non-admin: Allow editing own information
            JPanel userSettings = new JPanel(new BorderLayout());
            setupNonAdminUserPanel(userSettings, username);
            tabbedPane.add("Edit Profile", userSettings);
        }
    }

    private void setupUserManagementPanel(JPanel userSettings) {
        // Table for displaying users
        userTableModel = new DefaultTableModel(new String[] { "User ID", "Username", "Email", "Role" }, 0);
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Load existing users
        loadUsers();

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton addUserButton = new JButton("Add User");
        JButton removeUserButton = new JButton("Remove User");
        JButton updateUserRoleButton = new JButton("Update Role");
        JButton changePasswordButton = new JButton("Change Password");
        JButton backupButton = new JButton("Create Backup");

        buttonPanel.add(addUserButton);
        buttonPanel.add(removeUserButton);
        buttonPanel.add(updateUserRoleButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(backupButton);

        // Add action listeners to buttons
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        removeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeUser();
            }
        });

        updateUserRoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUserRole();
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });

        backupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createBackup();
            }
        });

        // Add components to the userSettings panel
        userSettings.add(scrollPane, BorderLayout.CENTER);
        userSettings.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupNonAdminUserPanel(JPanel userSettings, String username) {
        // Panel for editing user information
        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("New Password:"));
        panel.add(passwordField);

        // Add "Save" button to save updated information
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newUsername = usernameField.getText();
                String newPassword = new String(passwordField.getPassword());

                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(SettingsPanel.this, "Username and password cannot be empty.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Hash password
                String hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

                try {
                    String query = "UPDATE Users SET username = ?, password_hash = ? WHERE username = ?";
                    PreparedStatement stmt = Database.getConnection().prepareStatement(query);
                    stmt.setString(1, newUsername);
                    stmt.setString(2, hashedPassword);
                    stmt.setString(3, username); // Use the original username to identify the user
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(SettingsPanel.this, "User information updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(SettingsPanel.this,
                            "Error updating user information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(saveButton);
        userSettings.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void onLogout() {
    }

    private void loadUsers() {
        try {
            ResultSet rs = Database.getUserInfo("%"); // Fetch all users
            userTableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("user_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email"));
                row.add(rs.getString("role"));
                userTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleField = new JComboBox<>(new String[] { "admin", "user" });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add User", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String role = (String) roleField.getSelectedItem();

                // Hash password
                String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

                // Insert user into database
                String query = "INSERT INTO Users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = Database.getConnection().prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, email);
                stmt.setString(4, role);
                System.out.println(stmt.toString());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM Users WHERE user_id = ?";
                PreparedStatement stmt = Database.getConnection().prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User removed successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing user: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateUserRole() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String currentRole = (String) userTableModel.getValueAt(selectedRow, 3);
        JComboBox<String> roleField = new JComboBox<>(new String[] { "admin", "user" });
        roleField.setSelectedItem(currentRole);

        int result = JOptionPane.showConfirmDialog(this, roleField, "Update Role", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newRole = (String) roleField.getSelectedItem();

                String query = "UPDATE Users SET role = ? WHERE user_id = ?";
                PreparedStatement stmt = Database.getConnection().prepareStatement(query);
                stmt.setString(1, newRole);
                stmt.setInt(2, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User role updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating role: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changePassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to change password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        JPasswordField newPasswordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newPassword = new String(newPasswordField.getPassword());
                String hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

                String query = "UPDATE Users SET password_hash = ? WHERE user_id = ?";
                PreparedStatement stmt = Database.getConnection().prepareStatement(query);
                stmt.setString(1, hashedPassword);
                stmt.setInt(2, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating password: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createBackup() {
        // Simulating the creation of a backup (could be done by exporting to a SQL dump
        // or similar)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Backup");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Backup Files", "sql"));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File backupFile = fileChooser.getSelectedFile();
            try {
                Connection conn = Database.getConnection();
                BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile));

                // Get the list of all tables in the database
                DatabaseMetaData metadata = conn.getMetaData();
                ResultSet tables = metadata.getTables(null, null, null, new String[] { "TABLE" });

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    writer.write("-- Dumping table: " + tableName + "\n");
                    writer.write("DROP TABLE IF EXISTS " + tableName + ";\n");

                    // Get table structure (CREATE TABLE)
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
                    if (rs.next()) {
                        String createTableSQL = rs.getString(2); // The second column contains the CREATE TABLE
                                                                 // statement
                        writer.write(createTableSQL + ";\n\n");
                    }

                    // Get table data (INSERT INTO)
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM " + tableName);
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " (");

                        // Columns
                        for (int i = 1; i <= columnCount; i++) {
                            insertSQL.append(metaData.getColumnName(i));
                            if (i < columnCount) {
                                insertSQL.append(", ");
                            }
                        }

                        insertSQL.append(") VALUES (");

                        // Values
                        for (int i = 1; i <= columnCount; i++) {
                            String value = rs.getString(i);
                            if (value != null) {
                                insertSQL.append("'").append(value.replace("'", "''")).append("'");
                            } else {
                                insertSQL.append("NULL");
                            }
                            if (i < columnCount) {
                                insertSQL.append(", ");
                            }
                        }

                        insertSQL.append(");\n");
                        writer.write(insertSQL.toString());
                    }
                }
                String filePath = backupFile.getAbsolutePath();
                System.out.println("Creating backup at: " + filePath);

                // Simulate database backup process
                // BackupDatabase.dumpDatabase(filePath);

                JOptionPane.showMessageDialog(this, "Backup created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error creating backup: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
