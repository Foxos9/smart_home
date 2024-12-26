package smart_home.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import smart_home.database.Database;
import smart_home.utils.User;
import smart_home.utils.HelpRequest;

public class HelpPanel extends JPanel {

    private int userId; // For identifying the user
    private JTextArea messageArea;
    private JTextArea replyArea;
    private JButton submitButton;
    private JButton replyButton;
    private JTable requestTable;
    private DefaultTableModel requestTableModel;

    public HelpPanel(User user) {
        this.userId = user.getUserId();
        setLayout(new BorderLayout());

        // Set up the message area for user help request
        messageArea = new JTextArea(5, 20);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        // Setup the reply area for admins
        replyArea = new JTextArea(5, 20);
        replyArea.setWrapStyleWord(true);
        replyArea.setLineWrap(true);
        JScrollPane replyScrollPane = new JScrollPane(replyArea);

        // Submit request button for users
        submitButton = new JButton("Submit Help Request");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitHelpRequest();
            }
        });

        // Reply button for admins
        replyButton = new JButton("Submit Admin Reply");
        replyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAdminReply();
            }
        });

        // Create a table to display help requests
        requestTableModel = new DefaultTableModel(new String[] { "ID", "User ID", "Message", "Status", "Reply" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        requestTable = new JTable(requestTableModel);
        JScrollPane requestTableScrollPane = new JScrollPane(requestTable);

        // If the panel is for an admin, they can view all requests; otherwise, only the
        // user's requests
        if (user.isAdmin()) {
            loadPendingRequests(); // Admin views all requests
            replyButton.setEnabled(true);
        } else {
            loadUserRequests(); // User only sees their own requests
            submitButton.setEnabled(true);
        }

        // Layout adjustments
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Help Request Message:"), BorderLayout.NORTH);
        topPanel.add(messageScrollPane, BorderLayout.CENTER);
        topPanel.add(submitButton, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Admin Reply (for Admin):"), BorderLayout.NORTH);
        bottomPanel.add(replyScrollPane, BorderLayout.CENTER);
        bottomPanel.add(replyButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(requestTableScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Load the user's past help requests
    private void loadUserRequests() {
        clearTable();
        List<HelpRequest> requests = Database.getUserHelpRequests(userId);
        for (HelpRequest request : requests) {
            requestTableModel.addRow(new Object[] {
                    request.getId(),
                    userId,
                    request.getMessage(),
                    request.getStatus(),
                    request.getAdminReply()
            });
        }
    }

    // Load all pending help requests for admin
    private void loadPendingRequests() {
        clearTable();
        List<HelpRequest> requests = Database.getPendingHelpRequests();
        for (HelpRequest request : requests) {
            requestTableModel.addRow(new Object[] {
                    request.getId(),
                    request.getUserId(),
                    request.getMessage(),
                    request.getStatus(),
                    request.getAdminReply()
            });
        }
    }

    // Clear all rows from the table
    private void clearTable() {
        requestTableModel.setRowCount(0);
    }

    // Submit the user's help request
    private void submitHelpRequest() {
        String message = messageArea.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Database.submitHelpRequest(userId, message);
        JOptionPane.showMessageDialog(this, "Help request submitted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        messageArea.setText("");
        loadUserRequests(); // Reload the requests after submission
    }

    // Submit the admin's reply to a request
    private void submitAdminReply() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to reply to.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String replyMessage = replyArea.getText().trim();
        if (replyMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reply message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int requestId = (int) requestTableModel.getValueAt(selectedRow, 0);
        Database.submitAdminReply(requestId, replyMessage);
        JOptionPane.showMessageDialog(this, "Admin reply submitted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        replyArea.setText("");
        loadPendingRequests(); // Reload the pending requests after replying
    }
}
