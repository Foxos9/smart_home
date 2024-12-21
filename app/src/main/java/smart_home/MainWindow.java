package smart_home;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainWindow() {
        setTitle("Smart Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar
        Sidebar sidebar = new Sidebar(this);

        // Create content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add interface panels to CardLayout
        contentPanel.add(new HomePanel(), "Home");
        contentPanel.add(new JPanel(), "Interface2"); // Placeholder
        contentPanel.add(new JPanel(), "Interface3"); // Placeholder
        contentPanel.add(new JPanel(), "Interface4"); // Placeholder

        // Add components to main frame
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Show the home panel by default
        cardLayout.show(contentPanel, "Home");

        setVisible(true);
    }

    // Method to switch interfaces
    public void switchInterface(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
        });
    }
}
