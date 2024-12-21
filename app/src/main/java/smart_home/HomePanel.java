package smart_home;

import javax.swing.*;
import java.awt.*;

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
    }
}
