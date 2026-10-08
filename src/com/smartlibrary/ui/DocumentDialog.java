package com.smartlibrary.ui;

import com.smartlibrary.model.Document;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public final class DocumentDialog {
    public record Form(String type, String title, String author, int year, String topic, String abstractText) { }
    private DocumentDialog() { }

    public static Form show(Component parent, Document original) {
        JComboBox<String> type = new JComboBox<>(new String[] { "Book", "Paper" });
        JTextField title = new JTextField(25);
        JTextField author = new JTextField(25);
        JTextField year = new JTextField("2026", 8);
        JTextField topic = new JTextField(25);
        JTextArea abstractText = new JTextArea(5, 25);
        abstractText.setLineWrap(true);
        abstractText.setWrapStyleWord(true);
        if (original != null) {
            type.setSelectedItem(original.type()); title.setText(original.title());
            author.setText(original.author()); year.setText(Integer.toString(original.year()));
            topic.setText(original.topic()); abstractText.setText(original.abstractText());
        }
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 4));
        form.add(new JLabel("Type")); form.add(type);
        form.add(new JLabel("Title")); form.add(title);
        form.add(new JLabel("Author")); form.add(author);
        form.add(new JLabel("Year")); form.add(year);
        form.add(new JLabel("Topic")); form.add(topic);
        form.add(new JLabel("Abstract")); form.add(new JScrollPane(abstractText));
        while (true) {
            int result = JOptionPane.showConfirmDialog(parent, form,
                    original == null ? "Add document" : "Edit document", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return null;
            try {
                int parsedYear = Integer.parseInt(year.getText().trim());
                if (parsedYear < 1000 || parsedYear > 2100) throw new IllegalArgumentException("Enter a valid year.");
                if (title.getText().isBlank() || author.getText().isBlank() || topic.getText().isBlank()
                        || abstractText.getText().isBlank()) throw new IllegalArgumentException("Complete every field.");
                return new Form((String) type.getSelectedItem(), title.getText().trim(), author.getText().trim(),
                        parsedYear, topic.getText().trim(), abstractText.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Year must be a number.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage());
            }
        }
    }
}
