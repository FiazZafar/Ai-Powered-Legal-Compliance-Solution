package com.fyp.chatbot.models;

public class ChecklistModel {
    private int id;
    private String title;
    private String description;
    private String category;

    // Constructor
    public ChecklistModel(int id, String title, String description, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    // Getters (required for RecyclerView data binding)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
}
