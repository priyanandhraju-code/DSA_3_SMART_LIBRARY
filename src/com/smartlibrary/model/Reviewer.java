package com.smartlibrary.model;

import java.util.Set;

public record Reviewer(String name, Set<String> topics) {
    public boolean canReview(Document paper) {
        return topics.stream().anyMatch(topic -> topic.equalsIgnoreCase(paper.topic()));
    }
}
