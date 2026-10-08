package com.smartlibrary;

import com.smartlibrary.algorithm.BipartiteMatcher;
import com.smartlibrary.algorithm.SuffixArraySimilarity;
import com.smartlibrary.data.LibraryRepository;
import com.smartlibrary.model.Document;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.ReviewerService;
import com.smartlibrary.service.SearchService;
import com.smartlibrary.service.SimilarityService;
import com.smartlibrary.service.UsageService;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/** A console interface so the DSA work is easy to demonstrate in Eclipse. */
public final class ConsoleApp {
    private final Scanner input = new Scanner(System.in);
    private final LibraryService library;
    private final UsageService usage;
    private final SearchService search;
    private final SimilarityService similarity = new SimilarityService();
    private final ReviewerService reviewers = new ReviewerService();

    public ConsoleApp() {
        LibraryRepository repository = new LibraryRepository(Path.of("data"));
        library = new LibraryService(repository);
        usage = new UsageService(repository);
        search = new SearchService(library, usage);
    }

    public void run() {
        System.out.println("===============================================");
        System.out.println(" SMARTLIBRARY  |  JAVA DSA HACKATHON PROJECT");
        System.out.println("===============================================");
        while (true) {
            menu();
            String choice = read("Choose an option: ");
            if (choice == null || choice.equals("0")) { System.out.println("Goodbye!"); return; }
            switch (choice) {
                case "1" -> browse();
                case "2" -> search();
                case "3" -> openDocument();
                case "4" -> addDocument();
                case "5" -> editDocument();
                case "6" -> deleteDocument();
                case "7" -> compareDocuments();
                case "8" -> scanSimilarPairs();
                case "9" -> assignReviewers();
                case "10" -> dashboard();
                default -> System.out.println("Choose a number from 0 to 10.");
            }
        }
    }

    private void menu() {
        System.out.println("\n1  Browse catalogue          6  Delete document");
        System.out.println("2  Search documents          7  Compare two abstracts");
        System.out.println("3  Open document             8  Scan similar pairs");
        System.out.println("4  Add document              9  Assign paper reviewers");
        System.out.println("5  Edit document            10  Usage dashboard");
        System.out.println("0  Exit");
    }

    private void browse() {
        System.out.println("\nCATALOGUE (" + library.all().size() + " documents)");
        printDocuments(library.all());
    }

    private void search() {
        System.out.println("\nSEARCH MODE");
        System.out.println("1  Title / author / topic (KMP)");
        System.out.println("2  Abstract phrase (Rabin-Karp)");
        System.out.println("3  Multiple keywords, separated by commas (Aho-Corasick)");
        String mode = read("Mode: ");
        if (mode == null) return;
        SearchService.Mode selected = switch (mode) {
            case "1" -> SearchService.Mode.KEYWORD;
            case "2" -> SearchService.Mode.ABSTRACT;
            case "3" -> SearchService.Mode.MULTI_KEYWORD;
            default -> null;
        };
        if (selected == null) { System.out.println("Unknown search mode."); return; }
        String query = required("Keyword or phrase: ");
        if (query == null) return;
        SearchService.Result result = search.search(query, selected);
        System.out.println("\nRESULTS (" + result.documents().size() + ")");
        printDocuments(result.documents());
        if (!result.suggestion().isEmpty()) {
            System.out.println("Did you mean: " + result.suggestion() + "?");
        }
    }

    private void openDocument() {
        Document document = chooseDocument("Document ID to open: ");
        if (document == null) return;
        library.open(document.id());
        System.out.println("\n" + document.title() + " (" + document.type() + ", " + document.year() + ")");
        System.out.println("Author: " + document.author());
        System.out.println("Topic: " + document.topic());
        System.out.println("Abstract: " + document.abstractText());
        System.out.println("Views: " + document.views());
    }

    private void addDocument() {
        System.out.println("\nADD DOCUMENT");
        String type = readType(null);
        if (type == null) return;
        String title = required("Title: "); if (title == null) return;
        String author = required("Author: "); if (author == null) return;
        Integer year = readYear(null); if (year == null) return;
        String topic = required("Topic: "); if (topic == null) return;
        String abstractText = required("Abstract: "); if (abstractText == null) return;
        library.add(type, title, author, year, topic, abstractText);
        System.out.println("Document added and saved.");
    }

    private void editDocument() {
        Document document = chooseDocument("Document ID to edit: ");
        if (document == null) return;
        System.out.println("Press Enter to keep the value shown in brackets.");
        String type = readType(document.type()); if (type == null) return;
        String title = optional("Title [" + document.title() + "]: ", document.title()); if (title == null) return;
        String author = optional("Author [" + document.author() + "]: ", document.author()); if (author == null) return;
        Integer year = readYear(document.year()); if (year == null) return;
        String topic = optional("Topic [" + document.topic() + "]: ", document.topic()); if (topic == null) return;
        String abstractText = optional("Abstract [" + document.abstractText() + "]: ", document.abstractText());
        if (abstractText == null) return;
        library.update(document.id(), type, title, author, year, topic, abstractText);
        System.out.println("Document updated and saved.");
    }

    private void deleteDocument() {
        Document document = chooseDocument("Document ID to delete: ");
        if (document == null) return;
        String confirmation = read("Type YES to delete '" + document.title() + "': ");
        if ("YES".equals(confirmation)) {
            library.delete(document.id());
            System.out.println("Document deleted.");
        } else System.out.println("Deletion cancelled.");
    }

    private void compareDocuments() {
        Document first = chooseDocument("First document ID: "); if (first == null) return;
        Document second = chooseDocument("Second document ID: "); if (second == null) return;
        if (first.id() == second.id()) { System.out.println("Choose two different documents."); return; }
        SuffixArraySimilarity.Result result = similarity.compare(first, second);
        System.out.println("\nLONGEST SHARED PHRASE (Suffix Array + Kasai LCP)");
        System.out.println(first.title() + " vs. " + second.title());
        System.out.println("Shared length: " + result.length() + " characters");
        System.out.println("Phrase: " + (result.phrase().isBlank() ? "(none)" : result.phrase()));
        System.out.println("Phrase overlap is a review signal, not a plagiarism verdict.");
    }

    private void scanSimilarPairs() {
        SimilarityService.ConflictReport report = similarity.conflicts(library.all());
        System.out.println("\nSIMILAR DOCUMENT PAIRS (shared phrase >= 24 characters)");
        if (report.pairs().isEmpty()) System.out.println("None found.");
        else report.pairs().forEach(pair -> System.out.println("- " + pair));
        System.out.println("DOCUMENTS TO REVIEW (Vertex Cover 2-approximation)");
        if (report.reviewDocuments().isEmpty()) System.out.println("None needed.");
        else report.reviewDocuments().forEach(d -> System.out.println("- " + d.title()));
    }

    private void assignReviewers() {
        List<Document> papers = library.all().stream().filter(d -> d.type().equalsIgnoreCase("Paper")).toList();
        List<BipartiteMatcher.Assignment> assignments = reviewers.assign(library.all());
        Set<Integer> assigned = new HashSet<>();
        System.out.println("\nREVIEWER ASSIGNMENTS (Bipartite Matching)");
        for (BipartiteMatcher.Assignment assignment : assignments) {
            System.out.println("- " + assignment.paper().title() + " -> " + assignment.reviewer().name());
            assigned.add(assignment.paper().id());
        }
        for (Document paper : papers) if (!assigned.contains(paper.id())) {
            System.out.println("- Unassigned: " + paper.title() + " (no available compatible reviewer)");
        }
        System.out.println("Assigned " + assignments.size() + " of " + papers.size() + " papers.");
    }

    private void dashboard() {
        List<Document> documents = library.all();
        int totalViews = documents.stream().mapToInt(Document::views).sum();
        System.out.println("\nUSAGE DASHBOARD");
        System.out.println("Documents: " + documents.size());
        System.out.println("Searches: " + usage.searches());
        System.out.println("Total views: " + totalViews);
        System.out.println("Most viewed (Randomized QuickSort):");
        int rank = 1;
        for (Document document : usage.ranked(documents)) {
            System.out.printf("%2d. %-32s %d views%n", rank++, document.title(), document.views());
        }
        System.out.println("Sample search queries (Reservoir Sampling):");
        if (usage.sampleQueries().isEmpty()) System.out.println("- No searches yet.");
        else usage.sampleQueries().forEach(query -> System.out.println("- " + query));
    }

    private void printDocuments(List<Document> documents) {
        if (documents.isEmpty()) { System.out.println("No documents found."); return; }
        System.out.printf("%-4s %-6s %-34s %-23s %s%n", "ID", "Type", "Title", "Topic", "Views");
        for (Document d : documents) {
            System.out.printf("%-4d %-6s %-34s %-23s %d%n", d.id(), d.type(), d.title(), d.topic(), d.views());
        }
    }

    private Document chooseDocument(String prompt) {
        String raw = read(prompt);
        if (raw == null) return null;
        try {
            Document document = library.find(Integer.parseInt(raw));
            if (document != null) return document;
        } catch (NumberFormatException ignored) { }
        System.out.println("Document not found. Use Browse catalogue to see valid IDs.");
        return null;
    }

    private String readType(String current) {
        while (true) {
            String raw = read(current == null ? "Type (Book/Paper): " : "Type [" + current + "] (Book/Paper): ");
            if (raw == null) return null;
            if (raw.isBlank() && current != null) return current;
            if (raw.equalsIgnoreCase("Book")) return "Book";
            if (raw.equalsIgnoreCase("Paper")) return "Paper";
            System.out.println("Enter Book or Paper.");
        }
    }

    private Integer readYear(Integer current) {
        while (true) {
            String raw = read(current == null ? "Year: " : "Year [" + current + "]: ");
            if (raw == null) return null;
            if (raw.isBlank() && current != null) return current;
            try {
                int year = Integer.parseInt(raw);
                if (year >= 1000 && year <= 2100) return year;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a year from 1000 to 2100.");
        }
    }

    private String required(String prompt) {
        while (true) {
            String value = read(prompt);
            if (value == null) return null;
            if (!value.isBlank()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private String optional(String prompt, String current) {
        String value = read(prompt);
        return value == null ? null : value.isBlank() ? current : value;
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return input.hasNextLine() ? input.nextLine().trim() : null;
    }
}
