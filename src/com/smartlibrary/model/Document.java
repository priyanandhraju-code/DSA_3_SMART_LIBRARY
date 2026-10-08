package com.smartlibrary.model;

public class Document {
    private final int id;
    private String type;
    private String title;
    private String author;
    private int year;
    private String topic;
    private String abstractText;
    private int views;

    public Document(int id, String type, String title, String author, int year,
                    String topic, String abstractText, int views) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.author = author;
        this.year = year;
        this.topic = topic;
        this.abstractText = abstractText;
        this.views = views;
    }

    public int id() { return id; }
    public String type() { return type; }
    public String title() { return title; }
    public String author() { return author; }
    public int year() { return year; }
    public String topic() { return topic; }
    public String abstractText() { return abstractText; }
    public int views() { return views; }
    public void incrementViews() { views++; }

    public void update(String type, String title, String author, int year, String topic, String abstractText) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.year = year;
        this.topic = topic;
        this.abstractText = abstractText;
    }

    @Override public String toString() { return title + " (#" + id + ")"; }
}
