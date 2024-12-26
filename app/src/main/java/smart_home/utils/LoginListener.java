package smart_home.utils;

public interface LoginListener {
    void onLogin(String username, boolean isAdmin);
    void onLogout();
}

