package smart_home;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame implements LoginListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;

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

        // Add action listener to generate other panels

        JPanel loginWrapper = new JPanel(new GridBagLayout());
        loginWrapper.add(loginPanel);
        contentPanel.add(loginWrapper, "Login"); // Placeholder

        // Add components to main frame
        add(contentPanel, BorderLayout.CENTER);

        // Show the home panel by default
        cardLayout.show(contentPanel, "Login");

        setVisible(true);
        try {
            Database.connect("jdbc:mysql://localhost/SmartHome", "", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to switch interfaces
    public void switchInterface(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    @Override
    public void onLogin(String username, boolean isAdmin) {
        contentPanel.add(new HomePanel(), "Home");
        add(new Sidebar(this), BorderLayout.WEST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
        });
    }
}
