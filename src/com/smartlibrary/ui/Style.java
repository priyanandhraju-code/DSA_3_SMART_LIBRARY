package com.smartlibrary.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class Style {
    public static final Color BACKGROUND = new Color(246, 247, 244);
    public static final Color SURFACE = Color.WHITE;
    public static final Color GREEN = new Color(34, 85, 67);
    public static final Color INK = new Color(31, 43, 37);
    public static final Color MUTED = new Color(103, 115, 107);
    public static final Color BORDER = new Color(222, 228, 221);
    private Style() { }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        return panel;
    }

    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(INK);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(GREEN);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setFocusPainted(false);
        return button;
    }
}
