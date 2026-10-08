package com.smartlibrary.service;

import com.smartlibrary.data.LibraryRepository;
import com.smartlibrary.model.Document;
import java.util.ArrayList;
import java.util.List;

public class LibraryService {
    private final LibraryRepository repository;
    private final List<Document> documents;

    public LibraryService(LibraryRepository repository) {
        this.repository = repository;
        this.documents = repository.loadDocuments();
    }

    public List<Document> all() { return new ArrayList<>(documents); }
    public Document find(int id) {
        return documents.stream().filter(d -> d.id() == id).findFirst().orElse(null);
    }

    public void add(String type, String title, String author, int year, String topic, String abstractText) {
        int nextId = documents.stream().mapToInt(Document::id).max().orElse(0) + 1;
        documents.add(new Document(nextId, type, title, author, year, topic, abstractText, 0));
        repository.saveDocuments(documents);
    }

    public void update(int id, String type, String title, String author, int year, String topic, String abstractText) {
        Document document = find(id);
        if (document == null) throw new IllegalArgumentException("Document not found");
        document.update(type, title, author, year, topic, abstractText);
        repository.saveDocuments(documents);
    }

    public void delete(int id) {
        documents.removeIf(document -> document.id() == id);
        repository.saveDocuments(documents);
    }

    public void open(int id) {
        Document document = find(id);
        if (document == null) throw new IllegalArgumentException("Document not found");
        document.incrementViews();
        repository.saveDocuments(documents);
    }
}
