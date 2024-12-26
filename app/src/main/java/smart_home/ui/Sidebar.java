package smart_home.ui;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {

    public Sidebar(MainWindow mainWindow) {
        setLayout(new GridLayout(4, 1, 5, 5)); // Vertical layout with space between buttons
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(150, 0)); // Fixed width

        // Create buttons for navigation
        JButton loginButton = new JButton("Login");
        JButton homeButton = new JButton("Home");
        JButton helpButton = new JButton("Help");
        JButton settingsButton = new JButton("Settings");

        // Add listeners to buttons
        loginButton.addActionListener(e -> mainWindow.switchInterface("Login"));
        homeButton.addActionListener(e -> mainWindow.switchInterface("Home"));
        helpButton.addActionListener(e -> mainWindow.switchInterface("Help"));
        settingsButton.addActionListener(e -> mainWindow.switchInterface("Settings"));

        // Add buttons to the sidebar
        add(loginButton);
        add(homeButton);
        add(helpButton);
        add(settingsButton);
    }
}
