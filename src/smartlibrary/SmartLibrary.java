package smartlibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class SmartLibrary {
    private final List<Document> documents = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private int searchCount;

    public SmartLibrary() {
        documents.add(new Document(1, "Book", "The Shape of Data", "Mira Patel", 2023,
                "Data Structures", "An introduction to arrays, trees, and graphs with small examples.", 8));
        documents.add(new Document(2, "Book", "Learning Machines", "Jonas Lee", 2022,
                "Machine Learning", "A beginner guide to how computers learn patterns from data.", 12));
        documents.add(new Document(3, "Book", "A Field Guide to Climate", "Nora Williams", 2021,
                "Climate Science", "Explains climate change through measurements and simple models.", 5));
        documents.add(new Document(4, "Book", "The Quiet Internet", "Sam Rivera", 2024,
                "Computer Networks", "A clear introduction to packets, routers, and internet protocols.", 6));
        documents.add(new Document(5, "Paper", "Small Models, Big Ideas", "A. Chen", 2024,
                "Machine Learning", "A study of compact models for efficient machine learning.", 15));
        documents.add(new Document(6, "Paper", "Patterns in a Warming World", "E. Okafor", 2023,
                "Climate Science", "Compares climate observations and finds regional warming patterns.", 9));
        documents.add(new Document(7, "Paper", "Making Search More Helpful", "R. Singh", 2022,
                "Information Retrieval", "Studies keyword search and ranking in digital libraries.", 11));
        documents.add(new Document(8, "Paper", "Networks in Nature", "L. Morgan", 2021,
                "Data Structures", "Uses graphs to describe connections in natural systems.", 4));
    }

    public void run() {
        System.out.println("Welcome to SmartLibrary (Java DSA Demo)");
        while (true) {
            System.out.println("\n1. List documents");
            System.out.println("2. Search title, author, or topic (KMP)");
            System.out.println("3. Search abstracts (Rabin-Karp)");
            System.out.println("4. Open a document");
            System.out.println("5. Rank by views (Randomized QuickSort)");
            System.out.println("6. Show usage statistics");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listDocuments();
                case "2" -> searchMetadata();
                case "3" -> searchAbstracts();
                case "4" -> openDocument();
                case "5" -> rankByViews();
                case "6" -> showStats();
                case "0" -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Please choose a number from the menu.");
            }
        }
    }

    private void listDocuments() {
        System.out.println("\nLibrary collection:");
        documents.forEach(Document::printSummary);
    }

    private void searchMetadata() {
        String query = readQuery();
        if (query.isEmpty()) return;
        searchCount++;
        int matches = 0;
        for (Document document : documents) {
            String text = normalize(document.getTitle() + " " + document.getAuthor() + " " + document.getTopic());
            if (Algorithms.kmpContains(text, query)) {
                document.printSummary();
                matches++;
            }
        }
        showSearchResult(matches, query);
    }

    private void searchAbstracts() {
        String query = readQuery();
        if (query.isEmpty()) return;
        searchCount++;
        int matches = 0;
        for (Document document : documents) {
            if (Algorithms.rabinKarpContains(normalize(document.getAbstractText()), query)) {
                document.printSummary();
                matches++;
            }
        }
        showSearchResult(matches, query);
    }

    private String readQuery() {
        System.out.print("Enter keyword or phrase: ");
        String query = normalize(scanner.nextLine());
        if (query.isEmpty()) System.out.println("Please enter a search term.");
        return query;
    }

    private void showSearchResult(int matches, String query) {
        System.out.println("Results: " + matches);
        if (matches == 0 && !query.contains(" ")) {
            String suggestion = suggestWord(query);
            if (suggestion != null) System.out.println("Did you mean: " + suggestion + "?");
        }
    }

    private String suggestWord(String query) {
        Set<String> words = new TreeSet<>();
        for (Document document : documents) {
            String text = normalize(document.getTitle() + " " + document.getAuthor() + " " + document.getTopic());
            for (String word : text.split("[^a-z0-9]+")) if (!word.isEmpty()) words.add(word);
        }
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String word : words) {
            int distance = Algorithms.editDistance(query, word);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = word;
            }
        }
        return bestDistance <= Math.max(2, query.length() / 3) ? best : null;
    }

    private void openDocument() {
        System.out.print("Enter document ID: ");
        String input = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(input);
            for (Document document : documents) {
                if (document.getId() == id) {
                    document.addView();
                    document.printDetails();
                    return;
                }
            }
        } catch (NumberFormatException ignored) {
            // Invalid input is reported below.
        }
        System.out.println("Document not found. Choose an ID from the list.");
    }

    private void rankByViews() {
        Document[] ranked = documents.toArray(new Document[0]);
        Algorithms.sortByViews(ranked);
        System.out.println("\nMost viewed documents:");
        for (Document document : ranked) document.printSummary();
    }

    private void showStats() {
        System.out.println("Searches this session: " + searchCount);
        int totalViews = 0;
        for (Document document : documents) totalViews += document.getViews();
        System.out.println("Total sample views (including this session): " + totalViews);
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public static void main(String[] args) {
        new SmartLibrary().run();
    }
}
