package com.example.doan.model;

public class Book {
    private String title;
    private String category;

    public Book(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }
}

