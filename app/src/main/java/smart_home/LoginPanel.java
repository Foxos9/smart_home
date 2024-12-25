package smart_home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

enum LoginStatus {
    SUCCESS_ADMIN,
    SUCCESS_USER,
    FAILED_INVALID_CREDENTIALS,
    ERROR
}

public class LoginPanel extends JPanel implements ActionListener {
    private JLabel userLabel = new JLabel("User:");
    private JLabel passLabel = new JLabel("Password:");

    private JTextField userInput = new JTextField();
    private JPasswordField passInput = new JPasswordField();

    private JButton loginButton = new JButton("Login");
    private JLabel messageLabel = new JLabel();

    private Boolean root = false;

    // Dictionary to store username-password pairs
    private HashMap<String, String> credentials = new HashMap<>();
    private List<LoginListener> loginListeners = new ArrayList<>();

    public LoginPanel() {
        setLayout(new GridLayout(4, 1, 10, 10));

        JPanel userPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        userPanel.add(userLabel);
        userPanel.add(userInput);

        JPanel passPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        passPanel.add(passLabel);
        passPanel.add(passInput);

        add(userPanel);
        add(passPanel);
        add(loginButton);
        loginButton.addActionListener(this);
        add(messageLabel);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userInput.getText();
        String password = new String(passInput.getPassword()); // Secure password handling

        LoginStatus status = Database.validateLogin(username, password);
        switch (status) {
            case SUCCESS_ADMIN:
                messageLabel.setText("Login successful! Welcome.");
                messageLabel.setForeground(Color.GREEN);
                root = true; // Set the role as admin
                notifyLoginListeners(username, root);
                break;
            case SUCCESS_USER:
                messageLabel.setText("Login successful! Welcome.");
                messageLabel.setForeground(Color.GREEN);
                root = false; // Set the role as user
                notifyLoginListeners(username, root);
                break;
            case FAILED_INVALID_CREDENTIALS:
                messageLabel.setText("Invalid username or password.");
                messageLabel.setForeground(Color.RED);
                break;
            case ERROR:
                messageLabel.setText("An error occurred during login.");
                messageLabel.setForeground(Color.RED);
                break;
        }
        passInput.setText(""); // Clear the password field after checking
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public void addLoginListener(LoginListener listener) {
        loginListeners.add(listener);
    }

    private void notifyLoginListeners(String username, boolean isAdmin) {
        for (LoginListener listener : loginListeners) {
            listener.onLogin(username, isAdmin);
        }
    }
}
