package smart_home.utils;

import javax.swing.*;
import java.awt.*;

import smart_home.database.Database;

public class GarageDoorController extends JPanel {

    private JLabel statusLabel;
    private boolean isGarageOpen = false;
    private boolean isPromptActive = false; // Flag to prevent multiple prompts

    public GarageDoorController() {
        setLayout(new BorderLayout());

        // Status label to display messages
        statusLabel = new JLabel("Garage door is closed.");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.CENTER);
    }

    /**
     * Opens the garage door and updates the status.
     */
    public void openGarageDoor() {
        if (!isGarageOpen) {
            isGarageOpen = true;
            statusLabel.setText("Garage door is opening...");

            if (Database.updateGarageDoorStatus(true)) { // Set status to 'active'
                new Timer(2000, e -> {
                    statusLabel.setText("Garage door is open.");
                    if (Database.updateGarageDoorStatus(false)) { // Set status to 'inactive'
                        // Handle successful door opening and database update
                    } else {
                        statusLabel.setText("Error updating garage door status.");
                    }
                }).setRepeats(false);
            } else {
                statusLabel.setText("Error opening garage door.");
            }
        } else {
            statusLabel.setText("Garage door is already open.");
        }
    }

    /**
     * Closes the garage door and updates the status.
     */
    public void closeGarageDoor() {
        if (isGarageOpen) {
            isGarageOpen = false;
            statusLabel.setText("Garage door is closing...");
            // Simulate delay for closing
            new Timer(2000, e -> statusLabel.setText("Garage door is closed.")).setRepeats(false);
        } else {
            statusLabel.setText("Garage door is already closed.");
        }
    }

    /**
     * Handles RFID detection and prompts the user for confirmation to open the
     * garage door.
     */
    public void handleRFIDDetection() {
        if (isPromptActive) {
            statusLabel.setText("RFID detected. Waiting for response...");
            return;
        }

        isPromptActive = true; // Mark prompt as active
        SwingUtilities.invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "RFID detected. Do you want to open the garage door?",
                    "Confirm Action",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            isPromptActive = false; // Reset flag after user responds

            if (response == JOptionPane.YES_OPTION) {
                openGarageDoor();
            } else {
                statusLabel.setText("RFID detected. Action canceled.");
            }
        });
    }

    /**
     * Checks if the garage door is open.
     *
     * @return true if the garage door is open, false otherwise.
     */
    public boolean isGarageOpen() {
        return isGarageOpen;
    }

    /**
     * Sets a custom message on the status label.
     *
     * @param message the custom message to display.
     */
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}
