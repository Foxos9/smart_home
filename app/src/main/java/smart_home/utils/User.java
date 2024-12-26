package smart_home.utils;

public class User {
    private int userId; // New field for the user ID
    private String username;
    private String email;
    private boolean isAdmin;

    // Constructor with userId, username, isAdmin, and email
    public User(int userId, String username, String email, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username=" + username + ", email=" + email + ", isAdmin=" + isAdmin + "}";
    }
}
