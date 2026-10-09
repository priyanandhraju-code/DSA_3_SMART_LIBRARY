package com.smartlibrary.data;

import com.smartlibrary.model.Document;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

/** Saves the small demo collection and usage data in the project data directory. */
public class LibraryRepository {
    private final Path directory;
    private final Path documentsFile;
    private final Path usageFile;

    public LibraryRepository(Path directory) {
        this.directory = directory;
        documentsFile = directory.resolve("documents.csv");
        usageFile = directory.resolve("usage.properties");
    }

    public List<Document> loadDocuments() {
        if (!Files.exists(documentsFile)) {
            List<Document> sample = sampleDocuments();
            saveDocuments(sample);
            return sample;
        }
        return readDocuments();
    }

    private List<Document> readDocuments() {
        try {
            List<Document> documents = new ArrayList<>();
            for (String line : Files.readAllLines(documentsFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 8) throw new IOException("Invalid document row");
                documents.add(new Document(Integer.parseInt(parts[0]), decode(parts[1]), decode(parts[2]),
                        decode(parts[3]), Integer.parseInt(parts[4]), decode(parts[5]), decode(parts[6]),
                        Integer.parseInt(parts[7])));
            }
            return documents;
        } catch (IOException | IllegalArgumentException ex) {
            throw new IllegalStateException("Could not read " + documentsFile + ": " + ex.getMessage(), ex);
        }
    }

    public void saveDocuments(List<Document> documents) {
        try {
            Files.createDirectories(directory);
            List<String> lines = new ArrayList<>();
            for (Document d : documents) {
                lines.add(d.id() + "," + encode(d.type()) + "," + encode(d.title()) + ","
                        + encode(d.author()) + "," + d.year() + "," + encode(d.topic()) + ","
                        + encode(d.abstractText()) + "," + d.views());
            }
            Path temp = directory.resolve("documents.tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, documentsFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) { throw new UncheckedIOException(ex); }
    }

    public Properties loadUsage() {
        Properties usage = new Properties();
        if (!Files.exists(usageFile)) return usage;
        try (var reader = Files.newBufferedReader(usageFile, StandardCharsets.UTF_8)) {
            usage.load(reader);
            return usage;
        } catch (IOException ex) { throw new UncheckedIOException(ex); }
    }

    public void saveUsage(Properties usage) {
        try {
            Files.createDirectories(directory);
            Path temp = directory.resolve("usage.tmp");
            try (var writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                usage.store(writer, "SmartLibrary demo usage");
            }
            Files.move(temp, usageFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) { throw new UncheckedIOException(ex); }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static List<Document> sampleDocuments() {
        return new ArrayList<>(List.of(
                new Document(1, "Book", "The Shape of Data", "Mira Patel", 2023, "Data Structures",
                        "Arrays trees and graphs make information easier to organize. Graphs reveal connections between records.", 8),
                new Document(2, "Book", "Learning Machines", "Jonas Lee", 2022, "Machine Learning",
                        "Machine learning models find patterns in data. Small models can be useful when resources are limited.", 12),
                new Document(3, "Book", "A Field Guide to Climate", "Nora Williams", 2021, "Climate Science",
                        "Climate observations reveal warming patterns. Regional data helps explain changing weather.", 5),
                new Document(4, "Book", "The Quiet Internet", "Sam Rivera", 2024, "Computer Networks",
                        "Packets travel through networks. Routers and protocols keep the internet connected.", 6),
                new Document(5, "Paper", "Small Models, Big Ideas", "A. Chen", 2024, "Machine Learning",
                        "Machine learning models find patterns in data. This paper compares small models for efficient learning.", 15),
                new Document(6, "Paper", "Patterns in a Warming World", "E. Okafor", 2023, "Climate Science",
                        "Climate observations reveal warming patterns. This study compares regional temperature records.", 9),
                new Document(7, "Paper", "Making Search More Helpful", "R. Singh", 2022, "Information Retrieval",
                        "Keyword search and ranking help readers discover research papers in digital libraries.", 11),
                new Document(8, "Paper", "Networks in Nature", "L. Morgan", 2021, "Data Structures",
                        "Graphs reveal connections between records. This study models relationships in natural systems.", 4)
        ));
    }
}
