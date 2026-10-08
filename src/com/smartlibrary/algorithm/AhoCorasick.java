package com.smartlibrary.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** Searches all given keywords in one pass over the text. */
public final class AhoCorasick {
    private static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node fail;
        List<String> output = new ArrayList<>();
    }

    private AhoCorasick() { }

    public static Set<String> find(String text, List<String> patterns) {
        Node root = new Node();
        root.fail = root;
        for (String pattern : patterns) {
            if (pattern.isEmpty()) continue;
            Node node = root;
            for (char ch : pattern.toCharArray()) node = node.next.computeIfAbsent(ch, key -> new Node());
            node.output.add(pattern);
        }
        Queue<Node> queue = new ArrayDeque<>();
        for (Node child : root.next.values()) { child.fail = root; queue.add(child); }
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            for (Map.Entry<Character, Node> edge : node.next.entrySet()) {
                char ch = edge.getKey();
                Node child = edge.getValue();
                Node fallback = node.fail;
                while (fallback != root && !fallback.next.containsKey(ch)) fallback = fallback.fail;
                child.fail = fallback.next.getOrDefault(ch, root);
                child.output.addAll(child.fail.output);
                queue.add(child);
            }
        }
        Set<String> matches = new HashSet<>();
        Node state = root;
        for (char ch : text.toCharArray()) {
            while (state != root && !state.next.containsKey(ch)) state = state.fail;
            state = state.next.getOrDefault(ch, root);
            matches.addAll(state.output);
        }
        return matches;
    }
}
