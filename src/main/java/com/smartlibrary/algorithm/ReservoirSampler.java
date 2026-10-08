package com.smartlibrary.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Keeps a uniform sample of k search queries from a stream of unknown length. */
public final class ReservoirSampler {
    private ReservoirSampler() { }

    public static void add(List<String> sample, int capacity, long seen, String value) {
        if (sample.size() < capacity) { sample.add(value); return; }
        long position = ThreadLocalRandom.current().nextLong(seen);
        if (position < capacity) sample.set((int) position, value);
    }

    public static List<String> copy(List<String> sample) { return new ArrayList<>(sample); }
}
