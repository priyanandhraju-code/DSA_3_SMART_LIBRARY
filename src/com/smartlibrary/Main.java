package com.smartlibrary;

/** Run this class as a Java Application in Eclipse. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try {
            new ConsoleApp().run();
        } catch (RuntimeException ex) {
            System.err.println("SmartLibrary could not start: " + ex.getMessage());
            System.err.println("Check that the project folder is writable and try again.");
        }
    }
}
