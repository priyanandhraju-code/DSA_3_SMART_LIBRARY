package com.smartlibrary.algorithm;

import com.smartlibrary.model.Document;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomizedQuickSort {
    private RandomizedQuickSort() { }

    public static void byViews(List<Document> documents) { sort(documents, 0, documents.size() - 1); }

    private static void sort(List<Document> items, int low, int high) {
        if (low >= high) return;
        int random = ThreadLocalRandom.current().nextInt(low, high + 1);
        swap(items, random, high);
        Document pivot = items.get(high);
        int boundary = low;
        for (int i = low; i < high; i++) {
            Document current = items.get(i);
            if (current.views() > pivot.views() || (current.views() == pivot.views()
                    && current.title().compareToIgnoreCase(pivot.title()) < 0)) swap(items, boundary++, i);
        }
        swap(items, boundary, high);
        sort(items, low, boundary - 1);
        sort(items, boundary + 1, high);
    }

    private static void swap(List<Document> items, int a, int b) {
        Document temp = items.get(a); items.set(a, items.get(b)); items.set(b, temp);
    }
}
