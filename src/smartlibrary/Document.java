package smartlibrary;

public class Document {
    private final int id;
    private final String type;
    private final String title;
    private final String author;
    private final int year;
    private final String topic;
    private final String abstractText;
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

    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getTopic() { return topic; }
    public String getAbstractText() { return abstractText; }
    public int getViews() { return views; }
    public void addView() { views++; }

    public void printSummary() {
        System.out.printf("[%d] %-5s %-31s | %-18s | %d views%n",
                id, type, title, topic, views);
    }

    public void printDetails() {
        System.out.printf("%n[%d] %s (%s, %d)%n", id, title, type, year);
        System.out.println("Author: " + author);
        System.out.println("Topic: " + topic);
        System.out.println("Abstract: " + abstractText);
        System.out.println("Views: " + views);
    }
}
