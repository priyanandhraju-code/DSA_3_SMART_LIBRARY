package smartlibrary;

import java.util.Random;

public final class Algorithms {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 256L;
    private static final Random RANDOM = new Random();

    private Algorithms() { }

    // KMP: search a keyword in title, author, or topic.
    public static boolean kmpContains(String text, String pattern) {
        if (pattern.isEmpty()) return true;
        int[] lps = new int[pattern.length()];
        for (int i = 1, length = 0; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                lps[i++] = ++length;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }

        for (int i = 0, j = 0; i < text.length();) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) return true;
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return false;
    }

    // Rabin-Karp: rolling hash search in a document abstract.
    public static boolean rabinKarpContains(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return true;
        if (m > n) return false;

        long power = 1;
        long targetHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            if (i > 0) power = (power * BASE) % MOD;
            targetHash = (targetHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
        }

        for (int start = 0; start <= n - m; start++) {
            // Verify matching hashes because two different strings can have the same hash.
            if (windowHash == targetHash && text.regionMatches(start, pattern, 0, m)) {
                return true;
            }
            if (start < n - m) {
                windowHash = (windowHash - text.charAt(start) * power % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + text.charAt(start + m)) % MOD;
            }
        }
        return false;
    }

    // Levenshtein distance: insert, delete, or replace one character.
    public static int editDistance(String first, String second) {
        int[] previous = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) previous[j] = j;

        for (int i = 1; i <= first.length(); i++) {
            int[] current = new int[second.length() + 1];
            current[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int cost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1),
                        previous[j - 1] + cost);
            }
            previous = current;
        }
        return previous[second.length()];
    }

    // Randomized QuickSort: descending view count, then title for stable-looking ties.
    public static void sortByViews(Document[] documents) {
        quickSort(documents, 0, documents.length - 1);
    }

    private static void quickSort(Document[] documents, int left, int right) {
        if (left >= right) return;
        int randomIndex = left + RANDOM.nextInt(right - left + 1);
        swap(documents, randomIndex, right);
        Document pivot = documents[right];
        int boundary = left;
        for (int i = left; i < right; i++) {
            if (comesBefore(documents[i], pivot)) swap(documents, boundary++, i);
        }
        swap(documents, boundary, right);
        quickSort(documents, left, boundary - 1);
        quickSort(documents, boundary + 1, right);
    }

    private static boolean comesBefore(Document a, Document b) {
        if (a.getViews() != b.getViews()) return a.getViews() > b.getViews();
        return a.getTitle().compareToIgnoreCase(b.getTitle()) < 0;
    }

    private static void swap(Document[] documents, int a, int b) {
        Document temp = documents[a];
        documents[a] = documents[b];
        documents[b] = temp;
    }
}
