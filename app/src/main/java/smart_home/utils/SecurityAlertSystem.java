package smart_home.utils;

import javax.swing.*;
import java.awt.*;

public class SecurityAlertSystem extends JPanel {

    private JLabel statusLabel;

    public SecurityAlertSystem() {
        setLayout(new BorderLayout());

        // Initialize and configure status label
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.CENTER);
    }

    // Method to display normal system status
    public void displayNormalStatus() {
        updateStatus("Le système fonctionne");
    }

    // Method to display alert when movement is detected
    public void displayMovementAlert() {
        updateStatus("Alerte ! Mouvement détecté.");
    }

    // Method to display user away status
    public void displayUserAway() {
        updateStatus("Utilisateur absent. Surveillance activée.");
    }

    // Method to display user present status
    public void displayUserPresent() {
        updateStatus("Utilisateur présent. Surveillance désactivée.");
    }

    // Private helper method to update the status label
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message)); // Ensure thread-safe UI updates
    }
}
