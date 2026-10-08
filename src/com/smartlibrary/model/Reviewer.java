package com.smartlibrary.model;

import java.util.Set;

public final class Reviewer {
    private final String name;
    private final Set<String> topics;

    public Reviewer(String name, Set<String> topics) {
        this.name = name;
        this.topics = Set.copyOf(topics);
    }

    public String name() { return name; }
    public Set<String> topics() { return topics; }
    public boolean canReview(Document paper) {
        return topics.stream().anyMatch(topic -> topic.equalsIgnoreCase(paper.topic()));
    }
}
