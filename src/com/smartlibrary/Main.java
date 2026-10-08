package com.smartlibrary;

import com.smartlibrary.ui.SmartLibraryFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new SmartLibraryFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Could not start SmartLibrary: " + ex.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
