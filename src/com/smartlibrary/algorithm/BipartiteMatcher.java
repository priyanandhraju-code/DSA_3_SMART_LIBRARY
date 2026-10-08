package com.smartlibrary.algorithm;

import com.smartlibrary.model.Document;
import com.smartlibrary.model.Reviewer;
import java.util.ArrayList;
import java.util.List;

/** Augmenting-path matching: each reviewer and paper appears at most once. */
public final class BipartiteMatcher {
    public static final class Assignment {
        private final Document paper;
        private final Reviewer reviewer;
        public Assignment(Document paper, Reviewer reviewer) { this.paper = paper; this.reviewer = reviewer; }
        public Document paper() { return paper; }
        public Reviewer reviewer() { return reviewer; }
    }
    private BipartiteMatcher() { }

    public static List<Assignment> assign(List<Document> papers, List<Reviewer> reviewers) {
        int[] reviewerToPaper = new int[reviewers.size()];
        java.util.Arrays.fill(reviewerToPaper, -1);
        for (int paper = 0; paper < papers.size(); paper++) {
            boolean[] seen = new boolean[reviewers.size()];
            findPath(paper, papers, reviewers, reviewerToPaper, seen);
        }
        List<Assignment> result = new ArrayList<>();
        for (int reviewer = 0; reviewer < reviewers.size(); reviewer++) {
            if (reviewerToPaper[reviewer] >= 0) {
                result.add(new Assignment(papers.get(reviewerToPaper[reviewer]), reviewers.get(reviewer)));
            }
        }
        return result;
    }

    private static boolean findPath(int paper, List<Document> papers, List<Reviewer> reviewers,
                                    int[] reviewerToPaper, boolean[] seen) {
        for (int reviewer = 0; reviewer < reviewers.size(); reviewer++) {
            if (seen[reviewer] || !reviewers.get(reviewer).canReview(papers.get(paper))) continue;
            seen[reviewer] = true;
            if (reviewerToPaper[reviewer] == -1 || findPath(reviewerToPaper[reviewer], papers,
                    reviewers, reviewerToPaper, seen)) {
                reviewerToPaper[reviewer] = paper;
                return true;
            }
        }
        return false;
    }
}
