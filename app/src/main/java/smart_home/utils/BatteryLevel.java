package smart_home.utils;

import javax.swing.*;
import java.awt.*;

public class BatteryLevel extends JPanel {
    private int maxVoltage = 5; // Tension maximale (exemple : 5V)

    private double voltage = 2.5;

    public void setVoltage(double voltage) {
        this.voltage = voltage;
        repaint();
    }

    public void setMaxVoltage(int maxVoltage) {
        this.maxVoltage = maxVoltage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw battery level
        int levelWidth = (int) ((voltage / maxVoltage) * getWidth() / 2);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(getWidth() / 4, getHeight() / 4, levelWidth, getHeight() / 2);

        // Draw battery outline
        g2d.setColor(Color.BLACK);
        g2d.drawRect(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);

        // Draw voltage text
        g2d.setColor(Color.BLACK);
        g2d.drawString("Voltage: " + String.format("%.2f", voltage) + "V", 100, 30);
    }
}
