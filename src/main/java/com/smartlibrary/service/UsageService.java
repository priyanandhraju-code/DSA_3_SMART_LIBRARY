package com.smartlibrary.service;

import com.smartlibrary.algorithm.RandomizedQuickSort;
import com.smartlibrary.algorithm.ReservoirSampler;
import com.smartlibrary.data.LibraryRepository;
import com.smartlibrary.model.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UsageService {
    private final LibraryRepository repository;
    private final Properties properties;
    private final List<String> sample = new ArrayList<>();
    private long searches;

    public UsageService(LibraryRepository repository) {
        this.repository = repository;
        properties = repository.loadUsage();
        searches = Long.parseLong(properties.getProperty("searches", "0"));
        for (int i = 0; i < 5; i++) {
            String query = properties.getProperty("sample." + i);
            if (query != null) sample.add(query);
        }
    }

    public void recordSearch(String query) {
        searches++;
        ReservoirSampler.add(sample, 5, searches, query);
        properties.setProperty("searches", Long.toString(searches));
        for (int i = 0; i < sample.size(); i++) properties.setProperty("sample." + i, sample.get(i));
        repository.saveUsage(properties);
    }

    public long searches() { return searches; }
    public List<String> sampleQueries() { return ReservoirSampler.copy(sample); }
    public List<Document> ranked(List<Document> documents) {
        List<Document> ranked = new ArrayList<>(documents);
        RandomizedQuickSort.byViews(ranked);
        return ranked;
    }
}
