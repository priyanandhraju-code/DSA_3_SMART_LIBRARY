package com.smartlibrary.ui;

import com.smartlibrary.algorithm.SuffixArraySimilarity;
import com.smartlibrary.model.Document;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.SimilarityService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SimilarityPanel extends JPanel {
    private final LibraryService library;
    private final SimilarityService similarity;
    private final JComboBox<Document> first = new JComboBox<>();
    private final JComboBox<Document> second = new JComboBox<>();
    private final JTextArea output = new JTextArea();

    public SimilarityPanel(LibraryService library, SimilarityService similarity) {
        super(new BorderLayout(12, 12));
        this.library = library; this.similarity = similarity;
        setBackground(Style.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false);
        top.add(Style.heading("Document similarity"), BorderLayout.NORTH);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); controls.setOpaque(false);
        controls.add(first); controls.add(second);
        JButton compare = Style.primaryButton("Compare abstracts"); compare.addActionListener(event -> compare());
        JButton conflicts = new JButton("Scan similar pairs"); conflicts.addActionListener(event -> showConflicts());
        controls.add(compare); controls.add(conflicts); top.add(controls, BorderLayout.CENTER);
        top.add(Style.muted("Suffix array + Kasai LCP finds the longest shared phrase. Vertex Cover selects documents for review."), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        output.setEditable(false); output.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        output.setLineWrap(true); output.setWrapStyleWord(true);
        output.setMargin(new java.awt.Insets(18, 18, 18, 18));
        add(new JScrollPane(output), BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        int leftId = first.getSelectedItem() instanceof Document d ? d.id() : -1;
        int rightId = second.getSelectedItem() instanceof Document d ? d.id() : -1;
        first.removeAllItems(); second.removeAllItems();
        for (Document document : library.all()) { first.addItem(document); second.addItem(document); }
        selectId(first, leftId); selectId(second, rightId);
        if (rightId < 0 && second.getItemCount() > 1) second.setSelectedIndex(1);
        if (output.getText().isBlank()) output.setText("Choose two documents and click Compare abstracts.\n\nTry the two Machine Learning items or the two Climate Science items.");
    }

    private void selectId(JComboBox<Document> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) if (combo.getItemAt(i).id() == id) combo.setSelectedIndex(i);
    }

    private void compare() {
        Document a = (Document) first.getSelectedItem(), b = (Document) second.getSelectedItem();
        if (a == null || b == null) { output.setText("Add at least two documents."); return; }
        if (a.id() == b.id()) { output.setText("Choose two different documents."); return; }
        SuffixArraySimilarity.Result result = similarity.compare(a, b);
        output.setText("DOCUMENT A: " + a.title() + "\nDOCUMENT B: " + b.title() + "\n\n"
                + "Longest shared substring: " + result.length() + " characters\n\n"
                + (result.phrase().isBlank() ? "No shared phrase found." : "“" + result.phrase() + "”")
                + "\n\nThis is a phrase overlap signal, not a plagiarism verdict.");
    }

    private void showConflicts() {
        SimilarityService.ConflictReport report = similarity.conflicts(library.all());
        StringBuilder text = new StringBuilder("SIMILAR PAIRS (shared phrase of at least 24 characters)\n\n");
        if (report.pairs().isEmpty()) text.append("No pairs meet the threshold.\n");
        else report.pairs().forEach(pair -> text.append("• ").append(pair).append('\n'));
        text.append("\nDOCUMENTS TO REVIEW (Vertex Cover 2-approximation)\n\n");
        if (report.reviewDocuments().isEmpty()) text.append("None\n");
        else report.reviewDocuments().forEach(d -> text.append("• ").append(d.title()).append('\n'));
        text.append("\nEvery similar pair has at least one selected document.");
        output.setText(text.toString());
    }
}
