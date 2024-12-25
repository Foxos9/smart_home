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
        JButton interface3Button = new JButton("Interface 3");
        JButton interface4Button = new JButton("Interface 4");

        // Add listeners to buttons
        loginButton.addActionListener(e -> mainWindow.switchInterface("Login"));
        homeButton.addActionListener(e -> mainWindow.switchInterface("Home"));
        interface3Button.addActionListener(e -> mainWindow.switchInterface("Interface3"));
        interface4Button.addActionListener(e -> mainWindow.switchInterface("Interface4"));

        // Add buttons to the sidebar
        add(loginButton);
        add(homeButton);
        add(interface3Button);
        add(interface4Button);
    }
}
