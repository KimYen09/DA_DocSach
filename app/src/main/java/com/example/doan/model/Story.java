package com.example.doan.model;

import com.example.doan.R;

import java.util.HashMap;
import java.util.Map;

public class Story {
    private String id;
    private String title;
    private String description;
    private String category;
    private String type;
    private String imageResource;
    private String authorId;
    private Map<String, Chapter> chapters;

    public Story(String id, String title, String description, String category,
                 String imageResource, String type, String authorId, Map<String, Chapter> chapters) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.type = type;
        this.authorId = authorId;
        this.chapters = chapters != null ? chapters : new HashMap<>();
    }

    public Story(String id, String title, String description, String category, String imageName, String userId, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageName;
        this.authorId = userId;
        this.type = type;
    }
    public Story() {}


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAuthorId() {
        return authorId;
    }

    public Map<String, Chapter> getChapters() {
        return chapters != null ? chapters : new HashMap<>();
    }

    // Thêm các setter methods còn thiếu
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

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setChapters(Map<String, Chapter> chapters) {
        this.chapters = chapters;
    }
}
