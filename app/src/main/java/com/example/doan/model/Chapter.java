package com.example.doan.model;
public class Chapter {
    private String id;
    private String title;
    private String content;
    private String storyId; // Thêm storyId

    public Chapter() {
    }

    public Chapter(String id, String title, String content, String storyId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.storyId = storyId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStoryId() { // Thêm getter cho storyId
        return storyId;
    }

    // Setters - Cần thiết cho Firebase
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
}
