package smart_home.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import smart_home.utils.*;
import smart_home.database.Database;

public class MainWindow extends JFrame implements LoginListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;

    public MainWindow() {
        setTitle("Smart Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add interface panels to CardLayout
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.addLoginListener(this);
        loginPanel.setPreferredSize(new Dimension(600, 400));

        JPanel loginWrapper = new JPanel(new GridBagLayout());
        loginWrapper.add(loginPanel);
        contentPanel.add(loginWrapper, "Login"); // Placeholder

        contentPanel.add(new HomePanel(), "Home");

        // Add components to main frame
        add(contentPanel, BorderLayout.CENTER);

        // Show the home panel by default
        cardLayout.show(contentPanel, "Login");

        setVisible(true);
        try {
            Database.connect("jdbc:mysql://localhost/SmartHome", "yahya", "123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to switch interfaces
    public void switchInterface(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    @Override
    public void onLogin(User user) {
        sidebarPanel = new Sidebar(this);
        add(sidebarPanel, BorderLayout.WEST);

        HelpPanel helpPanel = new HelpPanel(user);
        contentPanel.add(helpPanel, "Help");

        SettingsPanel settingsPanel = new SettingsPanel(user);
        contentPanel.add(settingsPanel, "Settings");
    }

    @Override
    public void onLogout() {
        // Remove Sidebar from BorderLayout.WEST
        if (sidebarPanel != null) {
            remove(sidebarPanel);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
        });
    }
}
