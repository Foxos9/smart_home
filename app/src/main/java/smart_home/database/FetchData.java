package smart_home.database;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class FetchData extends JPanel {

    // Declare GUI components
    private JButton fetchButton;
    private JTextArea responseArea;

    // Lists to store data
    private List<Double> temperatureList = new ArrayList<>();
    private List<Double> humidityList = new ArrayList<>();
    private List<Integer> gasValueList = new ArrayList<>();
    private List<Integer> objectStatusList = new ArrayList<>();

    // Constructor to set up the GUI
    public FetchData() {
        // Set up the frame
        setLayout(new BorderLayout());

        // Input panel for ESP32 IP (not being used in this example, but can be for
        // dynamic IP entry)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        fetchButton = new JButton("Start"); // Button to fetch data
        inputPanel.add(fetchButton);

        // Response area where the fetched data will be displayed
        responseArea = new JTextArea();
        responseArea.setEditable(false); // Make the area read-only
        JScrollPane scrollPane = new JScrollPane(responseArea);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listener to the button
        // fetchButton.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // while(true) {
        // fetchMessage(); // Fetch the message when button is clicked
        //
        // }
        // }
        // });
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Run the fetch loop in a background thread
                        while (true) {
                            fetchMessage(); // Fetch the message
                            Thread.sleep(1000); // Wait for 5 seconds
                        }
                    }
                }.execute(); // Start the background task
            }
        });
    }

    // Method to fetch data from Firebase
    private void fetchMessage() {
        try {
            // Firebase URL
            String selectedNode = "test";
            URL url = new URL("https://autohouse-82c5f-default-rtdb.firebaseio.com/" + selectedNode + ".json");

            // Open HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            for (String key : jsonResponse.keySet()) {
                Object value = jsonResponse.get(key);

                switch (key.toLowerCase()) { // Case-insensitive comparison
                    case "temperature":
                        temperatureList.add(((Number) value).doubleValue());
                        break;
                    case "humidity":
                        humidityList.add(((Number) value).doubleValue());
                        break;
                    case "gaz_value":
                        gasValueList.add(((Number) value).intValue());
                        break;
                    case "object":
                        objectStatusList.add(((Number) value).intValue());
                        break;
                    default:
                        System.err.println("Unexpected key: " + key);
                        break;
                }
            }

            // Fetch the latest values for display and insertion
            Double lastTemperature = getLastTemperature();
            Double lastHumidity = getLastHumidity();
            Integer lastGasValue = getLastGasValue();
            Integer lastObjectStatus = getLastObjectStatus();

            // Display the fetched data in the response area
            String result = "Temperature: " + lastTemperature + "\n" +
                    "Humidity: " + lastHumidity + "\n" +
                    "Gas Value: " + lastGasValue + "\n" +
                    "Object Status: " + lastObjectStatus + "\n";
            responseArea.setText(result);

            // Insert the fetched data into the database for each device type
            // Assuming device_id for now is hardcoded, but this could be dynamic per device
            JSONObject state = new JSONObject();
            state.put("temperature", lastTemperature);
            Database.insertDeviceState(1, state.toString());

            state = new JSONObject();
            state.put("humidity", lastHumidity);
            Database.insertDeviceState(2, state.toString());

            state = new JSONObject();
            state.put("gas_value", lastGasValue);
            Database.insertDeviceState(3, state.toString());

            state = new JSONObject();
            state.put("object_status", lastObjectStatus);
            Database.insertDeviceState(4, state.toString());

        } catch (Exception ex) {
            responseArea.setText("Error fetching data: " + ex.getMessage());
        }
    }

    public Double getLastTemperature() {
        return temperatureList.isEmpty() ? null : temperatureList.get(temperatureList.size() - 1);
    }

    public Double getLastHumidity() {
        return humidityList.isEmpty() ? null : humidityList.get(humidityList.size() - 1);
    }

    public Integer getLastGasValue() {
        return gasValueList.isEmpty() ? null : gasValueList.get(gasValueList.size() - 1);
    }

    public Integer getLastObjectStatus() {
        return objectStatusList.isEmpty() ? null : objectStatusList.get(objectStatusList.size() - 1);
    }

}
