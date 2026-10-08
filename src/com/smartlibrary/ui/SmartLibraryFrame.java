package com.smartlibrary.ui;

import com.smartlibrary.data.LibraryRepository;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.ReviewerService;
import com.smartlibrary.service.SearchService;
import com.smartlibrary.service.SimilarityService;
import com.smartlibrary.service.UsageService;
import java.awt.BorderLayout;
import java.awt.Font;
import java.nio.file.Path;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class SmartLibraryFrame extends JFrame {
    private final CatalogPanel catalog;
    private final SimilarityPanel similarity;
    private final ReviewersPanel reviewers;
    private final DashboardPanel dashboard;

    public SmartLibraryFrame() {
        super("SmartLibrary — Java DSA Hackathon Project");
        LibraryRepository repository = new LibraryRepository(Path.of("data"));
        LibraryService library = new LibraryService(repository);
        UsageService usage = new UsageService(repository);
        SearchService search = new SearchService(library, usage);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720); setMinimumSize(new java.awt.Dimension(850, 560));
        setLocationRelativeTo(null); setLayout(new BorderLayout());
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Style.GREEN);
        banner.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 24, 18, 24));
        JLabel title = new JLabel("SMARTLIBRARY"); title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(java.awt.Color.WHITE); banner.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Discover  •  Compare  •  Assign  •  Analyze", SwingConstants.RIGHT);
        subtitle.setForeground(new java.awt.Color(215, 232, 219)); banner.add(subtitle, BorderLayout.EAST);
        add(banner, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        catalog = new CatalogPanel(library, search, this::refreshPanels);
        similarity = new SimilarityPanel(library, new SimilarityService());
        reviewers = new ReviewersPanel(library, new ReviewerService());
        dashboard = new DashboardPanel(library, usage);
        tabs.addTab("Catalogue & Search", catalog);
        tabs.addTab("Similarity", similarity);
        tabs.addTab("Reviewer Matching", reviewers);
        tabs.addTab("Dashboard", dashboard);
        add(tabs, BorderLayout.CENTER);
    }

    private void refreshPanels() {
        similarity.refresh(); reviewers.refresh(); dashboard.refresh();
    }
}
