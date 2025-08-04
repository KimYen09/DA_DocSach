package com.example.doan.model;

import java.io.Serializable;

public class Books implements Serializable {
    private String title;
    private String author;
    private String imageUrl;
    private String description;

    public Books(String title, String author, String imageUrl, String description) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
