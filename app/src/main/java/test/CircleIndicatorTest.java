package test;

import smart_home.utils.CircleIndicator;

import javax.swing.JFrame;
import javax.swing.Timer;

public class CircleIndicatorTest extends JFrame {
    private CircleIndicator temperatureIndicator;

    public CircleIndicatorTest() {
        super("Smart Home");
        this.setSize(800, 600);
        temperatureIndicator = new CircleIndicator(0, 100, "°C");
        add(temperatureIndicator);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Timer timer = new Timer(100, null); // Create a timer with 100ms interval
        timer.addActionListener(e -> {
            double currentValue = temperatureIndicator.getValue();

            if (currentValue < 100) {
                temperatureIndicator.setValue(currentValue + 1);
            } else {
                temperatureIndicator.setValue(0);
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        CircleIndicatorTest w = new CircleIndicatorTest();
    }
}
