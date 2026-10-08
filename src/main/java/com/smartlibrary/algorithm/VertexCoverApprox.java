package com.smartlibrary.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Picks both endpoints of each uncovered edge: a 2-approximation for vertex cover. */
public final class VertexCoverApprox {
    public record Edge(int first, int second) { }
    private VertexCoverApprox() { }

    public static Set<Integer> chooseDocumentsToReview(List<Edge> similarPairs) {
        Set<Integer> selected = new HashSet<>();
        for (Edge edge : similarPairs) {
            if (!selected.contains(edge.first()) && !selected.contains(edge.second())) {
                selected.add(edge.first());
                selected.add(edge.second());
            }
        }
        return selected;
    }

    public static List<Edge> similarPairs(List<String> abstracts, int minimumSharedLength) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < abstracts.size(); i++) {
            for (int j = i + 1; j < abstracts.size(); j++) {
                if (SuffixArraySimilarity.compare(abstracts.get(i), abstracts.get(j)).length() >= minimumSharedLength) {
                    edges.add(new Edge(i, j));
                }
            }
        }
        return edges;
    }
}
