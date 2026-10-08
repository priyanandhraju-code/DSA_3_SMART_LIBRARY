package com.smartlibrary.algorithm;

public final class KmpSearch {
    private KmpSearch() { }

    public static boolean contains(String text, String pattern) {
        if (pattern.isEmpty()) return true;
        int[] lps = new int[pattern.length()];
        for (int i = 1, length = 0; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(length)) lps[i++] = ++length;
            else if (length > 0) length = lps[length - 1];
            else lps[i++] = 0;
        }
        for (int i = 0, j = 0; i < text.length();) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == pattern.length()) return true;
            } else if (j > 0) j = lps[j - 1];
            else i++;
        }
        return false;
    }
}
