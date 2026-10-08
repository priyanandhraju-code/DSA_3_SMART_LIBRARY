package com.smartlibrary.service;

import com.smartlibrary.algorithm.AhoCorasick;
import com.smartlibrary.algorithm.KmpSearch;
import com.smartlibrary.algorithm.Levenshtein;
import com.smartlibrary.algorithm.RabinKarp;
import com.smartlibrary.model.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class SearchService {
    public enum Mode { KEYWORD, ABSTRACT, MULTI_KEYWORD }
    public record Result(List<Document> documents, String suggestion) { }
    private final LibraryService library;
    private final UsageService usage;

    public SearchService(LibraryService library, UsageService usage) {
        this.library = library;
        this.usage = usage;
    }

    public Result search(String rawQuery, Mode mode) {
        String query = normalize(rawQuery);
        if (query.isEmpty()) return new Result(library.all(), "");
        usage.recordSearch(query);
        List<String> keywords = Arrays.stream(query.split(","))
                .map(String::trim).filter(word -> !word.isEmpty()).distinct().toList();
        List<Document> matches = new ArrayList<>();
        for (Document document : library.all()) {
            String metadata = normalize(document.title() + " " + document.author() + " " + document.topic());
            String abstractText = normalize(document.abstractText());
            boolean found = switch (mode) {
                case KEYWORD -> KmpSearch.contains(metadata, query);
                case ABSTRACT -> RabinKarp.contains(abstractText, query);
                case MULTI_KEYWORD -> !AhoCorasick.find(metadata + " " + abstractText, keywords).isEmpty();
            };
            if (found) matches.add(document);
        }
        String suggestion = matches.isEmpty() && !query.contains(" ") && !query.contains(",")
                ? closestWord(query) : "";
        return new Result(matches, suggestion);
    }

    private String closestWord(String query) {
        Set<String> words = new TreeSet<>();
        for (Document document : library.all()) {
            String text = normalize(document.title() + " " + document.author() + " " + document.topic());
            for (String word : text.split("[^a-z0-9]+")) if (!word.isBlank()) words.add(word);
        }
        String best = "";
        int distance = Integer.MAX_VALUE;
        for (String word : words) {
            int current = Levenshtein.distance(query, word);
            if (current < distance) { distance = current; best = word; }
        }
        return distance <= Math.max(2, query.length() / 3) ? best : "";
    }

    public static String normalize(String value) { return value.trim().toLowerCase(Locale.ROOT); }
}
