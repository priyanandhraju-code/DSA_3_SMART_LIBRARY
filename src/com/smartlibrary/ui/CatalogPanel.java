package com.smartlibrary.ui;

import com.smartlibrary.model.Document;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.SearchService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

public class CatalogPanel extends JPanel {
    private final LibraryService library;
    private final SearchService search;
    private final Runnable refreshAll;
    private final JTextField query = new JTextField(22);
    private final JComboBox<String> mode = new JComboBox<>(new String[] {
            "Keyword (KMP)", "Abstract phrase (Rabin-Karp)", "Multiple keywords (Aho-Corasick)" });
    private final JLabel status = Style.muted("Browse the collection or enter a search.");
    private final JTextArea detail = new JTextArea();
    private final CatalogTable model = new CatalogTable();
    private final JTable table = new JTable(model);

    public CatalogPanel(LibraryService library, SearchService search, Runnable refreshAll) {
        super(new BorderLayout(12, 12));
        this.library = library; this.search = search; this.refreshAll = refreshAll;
        setBackground(Style.BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JPanel top = new JPanel(new BorderLayout(0, 12)); top.setOpaque(false);
        top.add(Style.heading("Library catalogue"), BorderLayout.NORTH);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); controls.setOpaque(false);
        controls.add(query); controls.add(mode);
        JButton find = Style.primaryButton("Search"); find.addActionListener(event -> runSearch());
        controls.add(find);
        JButton reset = new JButton("Show all"); reset.addActionListener(event -> { query.setText(""); refresh(); });
        controls.add(reset); top.add(controls, BorderLayout.CENTER); top.add(status, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        query.addActionListener(event -> runSearch());

        table.setRowHeight(30); table.setSelectionBackground(new java.awt.Color(221, 236, 226));
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(event -> showSelection());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(javax.swing.BorderFactory.createLineBorder(Style.BORDER));
        detail.setEditable(false); detail.setLineWrap(true); detail.setWrapStyleWord(true);
        detail.setText("Select a document to read its details.");
        detail.setMargin(new java.awt.Insets(12, 12, 12, 12));
        JSplitPane body = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, new JScrollPane(detail));
        body.setResizeWeight(0.68);
        body.setDividerLocation(330);
        body.setBorder(null);
        add(body, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); actions.setOpaque(false);
        JButton open = Style.primaryButton("Open / count view"); open.addActionListener(event -> openSelected());
        JButton add = new JButton("Add document"); add.addActionListener(event -> editDocument(null));
        JButton edit = new JButton("Edit selected"); edit.addActionListener(event -> {
            Document chosen = selected();
            if (chosen == null) status.setText("Select a document first.");
            else editDocument(chosen);
        });
        JButton delete = new JButton("Delete selected"); delete.addActionListener(event -> deleteSelected());
        actions.add(open); actions.add(add); actions.add(edit); actions.add(delete);
        add(actions, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        model.setDocuments(library.all());
        status.setText(model.getRowCount() + " documents in the catalogue. Multi-keyword mode accepts comma-separated words.");
        showSelection();
    }

    private void runSearch() {
        SearchService.Mode selectedMode = switch (mode.getSelectedIndex()) {
            case 1 -> SearchService.Mode.ABSTRACT;
            case 2 -> SearchService.Mode.MULTI_KEYWORD;
            default -> SearchService.Mode.KEYWORD;
        };
        SearchService.Result result = search.search(query.getText(), selectedMode);
        model.setDocuments(result.documents());
        String message = result.documents().size() + " result(s)";
        if (!result.suggestion().isEmpty()) message += "  •  Did you mean: " + result.suggestion() + "?";
        status.setText(message);
        refreshAll.run();
    }

    private Document selected() {
        int row = table.getSelectedRow();
        return row < 0 ? null : model.get(table.convertRowIndexToModel(row));
    }

    private void showSelection() {
        Document d = selected();
        detail.setText(d == null ? "Select a document to read its details." :
                d.title() + "\n" + d.type() + "  •  " + d.year() + "  •  " + d.topic() + "\n" +
                "By " + d.author() + "\n\n" + d.abstractText() + "\n\nViews: " + d.views());
    }

    private void openSelected() {
        Document d = selected();
        if (d == null) { status.setText("Select a document first."); return; }
        library.open(d.id()); model.fireTableDataChanged(); showSelection(); refreshAll.run();
    }

    private void editDocument(Document original) {
        DocumentDialog.Form form = DocumentDialog.show(this, original);
        if (form == null) return;
        if (original == null) library.add(form.type(), form.title(), form.author(), form.year(), form.topic(), form.abstractText());
        else library.update(original.id(), form.type(), form.title(), form.author(), form.year(), form.topic(), form.abstractText());
        query.setText(""); refresh(); refreshAll.run();
    }

    private void deleteSelected() {
        Document d = selected();
        if (d == null) { status.setText("Select a document first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete '" + d.title() + "'?", "Delete document",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        library.delete(d.id()); query.setText(""); refresh(); refreshAll.run();
    }

    private static class CatalogTable extends AbstractTableModel {
        private final String[] columns = { "ID", "Type", "Title", "Author", "Topic", "Year", "Views" };
        private List<Document> documents = new ArrayList<>();
        void setDocuments(List<Document> value) { documents = value; fireTableDataChanged(); }
        Document get(int index) { return documents.get(index); }
        @Override public int getRowCount() { return documents.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public Class<?> getColumnClass(int column) { return column == 0 || column >= 5 ? Integer.class : String.class; }
        @Override public Object getValueAt(int row, int column) {
            Document d = documents.get(row);
            return switch (column) {
                case 0 -> d.id(); case 1 -> d.type(); case 2 -> d.title(); case 3 -> d.author();
                case 4 -> d.topic(); case 5 -> d.year(); default -> d.views();
            };
        }
    }
}
