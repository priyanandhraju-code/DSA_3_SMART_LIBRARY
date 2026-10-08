package com.smartlibrary.algorithm;

public final class RabinKarp {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 256;
    private RabinKarp() { }

    public static boolean contains(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return true;
        if (m > n) return false;
        long power = 1, target = 0, window = 0;
        for (int i = 0; i < m; i++) {
            if (i > 0) power = power * BASE % MOD;
            target = (target * BASE + pattern.charAt(i)) % MOD;
            window = (window * BASE + text.charAt(i)) % MOD;
        }
        for (int start = 0; start <= n - m; start++) {
            if (window == target && text.regionMatches(start, pattern, 0, m)) return true;
            if (start < n - m) {
                window = (window - text.charAt(start) * power % MOD + MOD) % MOD;
                window = (window * BASE + text.charAt(start + m)) % MOD;
            }
        }
        return false;
    }
}
