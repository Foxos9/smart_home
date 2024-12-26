package smart_home.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import smart_home.utils.*;
import smart_home.database.Database;

public class LoginPanel extends JPanel implements ActionListener {
    private JLabel userLabel = new JLabel("User:");
    private JLabel passLabel = new JLabel("Password:");

    private JTextField userInput = new JTextField();
    private JPasswordField passInput = new JPasswordField();

    private JButton loginButton = new JButton("Login");
    private JLabel messageLabel = new JLabel();

    private Boolean loggedIn = false;

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
        if (loggedIn == false) {
            switch (status) {
                case SUCCESS_ADMIN:
                    messageLabel.setText("Login successful! Welcome.");
                    messageLabel.setForeground(Color.GREEN);
                    loginButton.setText("Log out");
                    notifyLoginListeners(Database.getUserInfo(username));
                    loggedIn = true;
                    break;
                case SUCCESS_USER:
                    messageLabel.setText("Login successful! Welcome.");
                    messageLabel.setForeground(Color.GREEN);
                    loginButton.setText("Log out");
                    notifyLoginListeners(Database.getUserInfo(username));
                    loggedIn = true;
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
        } else {
            messageLabel.setText("");
            loginButton.setText("Login");
            notifyLogout();
            loggedIn = false;
        }
    }

    public void addLoginListener(LoginListener listener) {
        loginListeners.add(listener);
    }

    private void notifyLoginListeners(User user) {
        for (LoginListener listener : loginListeners) {
            listener.onLogin(user);
        }
    }

    private void notifyLogout() {
        for (LoginListener listener : loginListeners) {
            listener.onLogout();
        }
    }

}
