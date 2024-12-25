package smart_home.ui;

import javax.swing.*;
import java.awt.*;

import smart_home.utils.*;
import smart_home.database.Database;

public class HomePanel extends JPanel {
    private BatteryLevel batteryLevel;
    private CircleIndicator temperatureIndicator;
    private CircleIndicator humidityIndicator;
    private SecurityAlertSystem presenceDetector;
    private GarageDoorController carDetector;

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create sensors container
        JPanel sensors = new JPanel(new GridLayout(3, 1, 0, 5));
        sensors.setBackground(Color.WHITE);

        // Create panels
        batteryLevel = new BatteryLevel();

        temperatureIndicator = new CircleIndicator(0, 100, "°C");

        humidityIndicator = new CircleIndicator(0, 100, "%");

        presenceDetector = new SecurityAlertSystem();
        presenceDetector.displayNormalStatus();

        carDetector = new GarageDoorController();

        JPanel secondRow = new JPanel(new GridLayout(1, 2, 5, 5));
        secondRow.setBackground(Color.WHITE);
        secondRow.add(temperatureIndicator);
        secondRow.add(humidityIndicator);

        JPanel thirdRow = new JPanel(new GridLayout(1, 2, 5, 5));
        thirdRow.setBackground(Color.WHITE);
        thirdRow.add(presenceDetector);
        thirdRow.add(carDetector);

        sensors.add(batteryLevel);
        sensors.add(secondRow);
        sensors.add(thirdRow);

        // Add to BorderLayout
        add(sensors, BorderLayout.CENTER);

        // Start a timer to update sensor values periodically
        Timer timer = new Timer(5000, e -> {
            updateSensorValues(); // Fetch and update sensor values
        }); // Update every 5 seconds
        timer.start();
    }

    private void updateSensorValues() {
        // Fetch the latest values from the database using the Database static methods
        double newTemperature = Database.getLatestSensorValue(1, "temperature");
        double newHumidity = Database.getLatestSensorValue(2, "humidity");
        boolean presenceStatus = Database.getLatestSensorValue(4, "object_status") > 0.5; // Example logic for presence
        int newBatteryLevel = (int) Database.getLatestSensorValue(5, "battery_level");

        // Update the components with new values
        temperatureIndicator.setValue(newTemperature);
        humidityIndicator.setValue(newHumidity);
        batteryLevel.setVoltage(newBatteryLevel);

        // Update presence and garage status
        // presenceDetector.setValues(presenceStatus);
        // carDetector.setValues(garageStatus);
    }
}
