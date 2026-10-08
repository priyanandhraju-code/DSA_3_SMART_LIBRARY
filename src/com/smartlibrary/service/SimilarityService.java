package com.smartlibrary.service;

import com.smartlibrary.algorithm.SuffixArraySimilarity;
import com.smartlibrary.algorithm.VertexCoverApprox;
import com.smartlibrary.model.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimilarityService {
    public static final class ConflictReport {
        private final List<String> pairs;
        private final List<Document> reviewDocuments;
        public ConflictReport(List<String> pairs, List<Document> reviewDocuments) {
            this.pairs = pairs; this.reviewDocuments = reviewDocuments;
        }
        public List<String> pairs() { return pairs; }
        public List<Document> reviewDocuments() { return reviewDocuments; }
    }
    private static final int MIN_SHARED_PHRASE = 24;

    public SuffixArraySimilarity.Result compare(Document first, Document second) {
        return SuffixArraySimilarity.compare(SearchService.normalize(first.abstractText()),
                SearchService.normalize(second.abstractText()));
    }

    public ConflictReport conflicts(List<Document> documents) {
        List<String> abstracts = documents.stream().map(d -> SearchService.normalize(d.abstractText())).toList();
        List<VertexCoverApprox.Edge> edges = VertexCoverApprox.similarPairs(abstracts, MIN_SHARED_PHRASE);
        Set<Integer> selected = VertexCoverApprox.chooseDocumentsToReview(edges);
        List<String> pairs = new ArrayList<>();
        for (VertexCoverApprox.Edge edge : edges) {
            pairs.add(documents.get(edge.first()).title() + "  <->  " + documents.get(edge.second()).title());
        }
        List<Document> review = selected.stream().sorted().map(documents::get).toList();
        return new ConflictReport(pairs, review);
    }
}
