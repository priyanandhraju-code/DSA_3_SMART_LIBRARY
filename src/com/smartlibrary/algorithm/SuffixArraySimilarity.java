package com.smartlibrary.algorithm;

import java.util.Arrays;

/** Longest shared substring found with a prefix-doubling suffix array and Kasai LCP. */
public final class SuffixArraySimilarity {
    public static final class Result {
        private final String phrase;
        private final int length;

        public Result(String phrase, int length) {
            this.phrase = phrase;
            this.length = length;
        }

        public String phrase() { return phrase; }
        public int length() { return length; }
    }
    private SuffixArraySimilarity() { }

    public static Result compare(String first, String second) {
        if (first.isEmpty() || second.isEmpty()) return new Result("", 0);
        // The separators are outside normal user text and cannot cross from one document to the other.
        String text = first + '\u0001' + second + '\u0000';
        int n = text.length();
        Integer[] order = new Integer[n];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) { order[i] = i; rank[i] = text.charAt(i); }
        for (int width = 1; width < n; width *= 2) {
            final int step = width;
            final int[] current = rank;
            Arrays.sort(order, (a, b) -> {
                int firstRank = Integer.compare(current[a], current[b]);
                if (firstRank != 0) return firstRank;
                return Integer.compare(a + step < n ? current[a + step] : -1,
                        b + step < n ? current[b + step] : -1);
            });
            int[] next = new int[n];
            for (int i = 1; i < n; i++) {
                int a = order[i - 1], b = order[i];
                next[b] = next[a] + (current[a] != current[b]
                        || (a + step < n ? current[a + step] : -1) != (b + step < n ? current[b + step] : -1) ? 1 : 0);
            }
            rank = next;
            if (rank[order[n - 1]] == n - 1) break;
        }
        int[] position = new int[n];
        for (int i = 0; i < n; i++) position[order[i]] = i;
        int bestLength = 0, bestStart = 0, shared = 0;
        for (int i = 0; i < n; i++) {
            int pos = position[i];
            if (pos == 0) continue;
            int other = order[pos - 1];
            while (i + shared < n && other + shared < n && text.charAt(i + shared) == text.charAt(other + shared)) shared++;
            boolean differentDocuments = (i < first.length() && other > first.length() && other < n - 1)
                    || (other < first.length() && i > first.length() && i < n - 1);
            if (differentDocuments && shared > bestLength) { bestLength = shared; bestStart = i; }
            if (shared > 0) shared--;
        }
        return new Result(text.substring(bestStart, bestStart + bestLength).trim(), bestLength);
    }
}
