package test;

import smart_home.*;

import java.util.Random;

import javax.swing.*;

public class BatteryLevelTest extends JFrame {
    private static final int MAX_VOLTAGE = 5; // Tension maximale (exemple : 5V)
    private static final int MIN_VOLTAGE = 0; // Tension minimale (exemple : 0V)

    private BatteryLevel batteryLevel;

    public BatteryLevelTest() {
        setTitle("Battery Level Display");
        setSize(300, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        batteryLevel = new BatteryLevel();
        add(batteryLevel);
    }

    public void updateBatteryLevel(double voltage) {
        batteryLevel.setVoltage(voltage);
        batteryLevel.repaint();
    }

    public static void main(String[] args) {
        BatteryLevelTest frame = new BatteryLevelTest();
        frame.setVisible(true);

        Random random = new Random();

        // Simulate reading voltage values in a loop
        while (true) {
            double simulatedVoltage = MIN_VOLTAGE + (MAX_VOLTAGE - MIN_VOLTAGE) * random.nextDouble();
            frame.updateBatteryLevel(simulatedVoltage);
            try {
                Thread.sleep(1000); // Mise à jour toutes les secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
