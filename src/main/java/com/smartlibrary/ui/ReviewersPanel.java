package com.smartlibrary.ui;

import com.smartlibrary.algorithm.BipartiteMatcher;
import com.smartlibrary.model.Document;
import com.smartlibrary.model.Reviewer;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.ReviewerService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReviewersPanel extends JPanel {
    private final LibraryService library;
    private final ReviewerService service;
    private final JTextArea output = new JTextArea();

    public ReviewersPanel(LibraryService library, ReviewerService service) {
        super(new BorderLayout(12, 12));
        this.library = library; this.service = service;
        setBackground(Style.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false);
        top.add(Style.heading("Research paper reviewers"), BorderLayout.NORTH);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); actions.setOpaque(false);
        JButton assign = Style.primaryButton("Assign reviewers"); assign.addActionListener(event -> refresh());
        actions.add(assign); top.add(actions, BorderLayout.CENTER);
        top.add(Style.muted("Bipartite matching assigns at most one paper to each reviewer using topic expertise."), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        output.setEditable(false); output.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        output.setMargin(new java.awt.Insets(18, 18, 18, 18));
        add(new JScrollPane(output), BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        List<Document> papers = library.all().stream().filter(d -> d.type().equalsIgnoreCase("Paper")).toList();
        List<BipartiteMatcher.Assignment> assignments = service.assign(library.all());
        Set<Integer> assignedIds = new HashSet<>();
        StringBuilder text = new StringBuilder("REVIEWER EXPERTISE\n\n");
        for (Reviewer reviewer : service.reviewers()) {
            text.append("• ").append(reviewer.name()).append(" — ").append(String.join(", ", reviewer.topics())).append('\n');
        }
        text.append("\nPAPER ASSIGNMENTS\n\n");
        for (BipartiteMatcher.Assignment assignment : assignments) {
            text.append("• ").append(assignment.paper().title()).append(" → ").append(assignment.reviewer().name()).append('\n');
            assignedIds.add(assignment.paper().id());
        }
        if (assignments.isEmpty()) text.append("No compatible assignments.\n");
        text.append("\nAssigned ").append(assignments.size()).append(" of ").append(papers.size()).append(" papers.\n");
        for (Document paper : papers) if (!assignedIds.contains(paper.id())) text.append("Unassigned: ").append(paper.title()).append('\n');
        output.setText(text.toString());
    }
}
