package com.smartlibrary;

import com.smartlibrary.algorithm.AhoCorasick;
import com.smartlibrary.algorithm.BipartiteMatcher;
import com.smartlibrary.algorithm.KmpSearch;
import com.smartlibrary.algorithm.Levenshtein;
import com.smartlibrary.algorithm.RabinKarp;
import com.smartlibrary.algorithm.RandomizedQuickSort;
import com.smartlibrary.algorithm.ReservoirSampler;
import com.smartlibrary.algorithm.SuffixArraySimilarity;
import com.smartlibrary.algorithm.VertexCoverApprox;
import com.smartlibrary.model.Document;
import com.smartlibrary.model.Reviewer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmTest {
    @Test void searchesFindExpectedPatterns() {
        assertTrue(KmpSearch.contains("ababababac", "ababac"));
        assertFalse(KmpSearch.contains("digital library", "paper"));
        assertTrue(RabinKarp.contains("the graph connects papers", "connects"));
        assertFalse(RabinKarp.contains("short", "longer pattern"));
        assertEquals(Set.of("climate", "data"),
                AhoCorasick.find("climate data", List.of("climate", "data", "graph")));
    }

    @Test void typoAndSimilarityWork() {
        assertEquals(2, Levenshtein.distance("machien", "machine"));
        assertEquals("shared phrase", SuffixArraySimilarity.compare(
                "first shared phrase here", "another shared phrase there").phrase());
    }

    @Test void matchingAndCoverRespectConstraints() {
        Document climate = doc(1, "Climate", "Climate Science", 1);
        Document machine = doc(2, "Machine", "Machine Learning", 2);
        List<BipartiteMatcher.Assignment> assignments = BipartiteMatcher.assign(
                List.of(climate, machine), List.of(
                        new Reviewer("A", Set.of("Climate Science")),
                        new Reviewer("B", Set.of("Machine Learning"))));
        assertEquals(2, assignments.size());
        List<VertexCoverApprox.Edge> edges = List.of(new VertexCoverApprox.Edge(0, 1),
                new VertexCoverApprox.Edge(1, 2));
        Set<Integer> selected = VertexCoverApprox.chooseDocumentsToReview(edges);
        for (VertexCoverApprox.Edge edge : edges) {
            assertTrue(selected.contains(edge.first()) || selected.contains(edge.second()));
        }
    }

    @Test void rankingAndSamplingStayBounded() {
        List<Document> documents = new ArrayList<>(List.of(doc(1, "A", "Topic", 1),
                doc(2, "B", "Topic", 5), doc(3, "C", "Topic", 3)));
        RandomizedQuickSort.byViews(documents);
        assertEquals(List.of(5, 3, 1), documents.stream().map(Document::views).toList());
        List<String> sample = new ArrayList<>();
        for (int i = 1; i <= 100; i++) ReservoirSampler.add(sample, 5, i, "query" + i);
        assertEquals(5, sample.size());
    }

    private Document doc(int id, String title, String topic, int views) {
        return new Document(id, "Paper", title, "Author", 2025, topic, "Abstract", views);
    }
}
