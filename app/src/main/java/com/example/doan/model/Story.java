package com.example.doan.model;

import java.util.HashMap;
import java.util.Map;

public class Story {
    private String id;
    private String title;
    private String description;
    private String category;
    private String imageResource;
    private String userId;
    private String type;
    private HashMap<String, Object> chapters;
    private String creationDate;
    private long viewCount;
    private boolean isPremium; // Thêm trường này để đánh dấu truyện premium

    // Constructor mặc định (cần thiết cho Firebase)
    public Story() {
        this.isPremium = false; // Mặc định không phải premium
    }

    // Constructor đầy đủ
    public Story(String id, String title, String description, String category,
                 String imageResource, String type, String userId,
                 HashMap<String, Object> chapters, String creationDate, long viewCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.type = type;
        this.userId = userId;
        this.chapters = chapters;
        this.creationDate = creationDate;
        this.viewCount = viewCount;
        this.isPremium = false; // Mặc định không phải premium
    }

    // Constructor với isPremium
    public Story(String id, String title, String description, String category,
                 String imageResource, String type, String userId,
                 HashMap<String, Object> chapters, String creationDate, long viewCount, boolean isPremium) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.type = type;
        this.userId = userId;
        this.chapters = chapters;
        this.creationDate = creationDate;
        this.viewCount = viewCount;
        this.isPremium = isPremium;
    }

    // Constructor với 9 tham số (cho AddStory/EditStory)
    public Story(String id, String title, String description, String category,
                 String imageResource, String type, String userId,
                 HashMap<String, Object> chapters, String creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.type = type;
        this.userId = userId;
        this.chapters = chapters;
        this.creationDate = creationDate;
        this.viewCount = 0;
        this.isPremium = false; // Mặc định không phải premium
    }

    // Constructor với 8 tham số
    public Story(String id, String title, String description, String category,
                 String imageResource, String userId, String type,
                 HashMap<String, Object> chapters) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.userId = userId;
        this.type = type;
        this.chapters = chapters;
        this.creationDate = "";
        this.viewCount = 0;
        this.isPremium = false; // Mặc định không phải premium
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getUserId() {
        return userId;
    }

    // Getter cho authorId (alias cho userId để tương thích)
    public String getAuthorId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, Object> getChapters() {
        return chapters;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public long getViewCount() {
        return viewCount;
    }

    public boolean isPremium() {
        return isPremium;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Setter cho authorId (alias cho userId để tương thích)
    public void setAuthorId(String authorId) {
        this.userId = authorId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setChapters(HashMap<String, Object> chapters) {
        this.chapters = chapters;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public void setPremium(boolean premium) {
        this.isPremium = premium;
    }
}
