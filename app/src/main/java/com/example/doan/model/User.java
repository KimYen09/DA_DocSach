package com.example.doan.model;

public class User {
    private String userId;
    private String username;
    private String email;
    private String avatar;
    private String joinDate;
    private int storyCount;
    private int likes;
    private String role;

    public User() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public User(String userId, String username, String email, String avatar, String joinDate, int storyCount, int likes, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.joinDate = joinDate;
        this.storyCount = storyCount;
        this.likes = likes;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public int getStoryCount() {
        return storyCount;
    }

    public int getLikes() {
        return likes;
    }

    public String getRole() {
        return role;
    }
}
