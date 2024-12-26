package smart_home.utils;

public class HelpRequest {
    private int id;
    private int userId;
    private String message;
    private String adminReply;
    private String status; // Add status field

    public HelpRequest(int id, int userId, String message, String adminReply, String status) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.adminReply = adminReply;
        this.status = status;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public String getStatus() {
        return status;
    }
}
