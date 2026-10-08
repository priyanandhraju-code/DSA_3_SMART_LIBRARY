package com.smartlibrary.service;

import com.smartlibrary.algorithm.BipartiteMatcher;
import com.smartlibrary.model.Document;
import com.smartlibrary.model.Reviewer;
import java.util.List;
import java.util.Set;

public class ReviewerService {
    private final List<Reviewer> reviewers = List.of(
            new Reviewer("Dr. Asha Mehta", Set.of("Machine Learning", "Data Structures")),
            new Reviewer("Dr. Leo Grant", Set.of("Climate Science")),
            new Reviewer("Dr. Sofia Chen", Set.of("Information Retrieval", "Machine Learning")),
            new Reviewer("Dr. Omar Ali", Set.of("Data Structures", "Computer Networks"))
    );

    public List<Reviewer> reviewers() { return reviewers; }
    public List<BipartiteMatcher.Assignment> assign(List<Document> documents) {
        List<Document> papers = documents.stream().filter(d -> d.type().equalsIgnoreCase("Paper")).toList();
        return BipartiteMatcher.assign(papers, reviewers);
    }
}
