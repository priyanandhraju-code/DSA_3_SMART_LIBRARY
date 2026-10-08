package com.smartlibrary.algorithm;

public final class Levenshtein {
    private Levenshtein() { }

    public static int distance(String first, String second) {
        int[] previous = new int[second.length() + 1];
        for (int j = 0; j < previous.length; j++) previous[j] = j;
        for (int i = 1; i <= first.length(); i++) {
            int[] current = new int[second.length() + 1];
            current[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int cost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + cost);
            }
            previous = current;
        }
        return previous[second.length()];
    }
}
