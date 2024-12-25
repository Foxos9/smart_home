package test;

import javax.swing.*;

import smart_home.*;

public class FetchDataTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FetchData gui = new FetchData();
            gui.setVisible(true);
        });
    }
}
