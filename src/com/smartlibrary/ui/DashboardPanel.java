package com.smartlibrary.ui;

import com.smartlibrary.model.Document;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.UsageService;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DashboardPanel extends JPanel {
    private final LibraryService library;
    private final UsageService usage;
    private final JLabel documents = Style.heading("0");
    private final JLabel papers = Style.heading("0");
    private final JLabel searches = Style.heading("0");
    private final JLabel views = Style.heading("0");
    private final JTextArea ranking = new JTextArea();

    public DashboardPanel(LibraryService library, UsageService usage) {
        super(new BorderLayout(12, 12));
        this.library = library; this.usage = usage;
        setBackground(Style.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JPanel top = new JPanel(new BorderLayout(0, 14)); top.setOpaque(false);
        top.add(Style.heading("Library dashboard"), BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0)); cards.setOpaque(false);
        cards.add(metric("DOCUMENTS", documents)); cards.add(metric("RESEARCH PAPERS", papers));
        cards.add(metric("SEARCHES", searches)); cards.add(metric("TOTAL VIEWS", views));
        top.add(cards, BorderLayout.CENTER); add(top, BorderLayout.NORTH);
        ranking.setEditable(false); ranking.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        ranking.setMargin(new java.awt.Insets(18, 18, 18, 18));
        add(new JScrollPane(ranking), BorderLayout.CENTER);
        refresh();
    }

    private JPanel metric(String title, JLabel value) {
        JPanel card = Style.card(); card.setLayout(new BorderLayout(0, 6));
        card.add(Style.muted(title), BorderLayout.NORTH); card.add(value, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        List<Document> all = library.all();
        documents.setText(Integer.toString(all.size()));
        papers.setText(Long.toString(all.stream().filter(d -> d.type().equalsIgnoreCase("Paper")).count()));
        searches.setText(Long.toString(usage.searches()));
        views.setText(Integer.toString(all.stream().mapToInt(Document::views).sum()));
        StringBuilder text = new StringBuilder("MOST VIEWED DOCUMENTS  •  Randomized QuickSort\n\n");
        int position = 1;
        for (Document document : usage.ranked(all)) {
            text.append(String.format("%d. %-34s %d views%n", position++, document.title(), document.views()));
        }
        text.append("\nSAMPLE OF SEARCH QUERIES  •  Reservoir Sampling\n\n");
        if (usage.sampleQueries().isEmpty()) text.append("Search the catalogue to collect usage data.\n");
        else usage.sampleQueries().forEach(query -> text.append("• ").append(query).append('\n'));
        text.append("\nData is saved locally in the data folder.");
        ranking.setText(text.toString());
    }
}
