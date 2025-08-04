package com.example.doan.model;

import java.util.HashMap;
import java.util.Map;

public class Comment {
    private String commentId;
    private String userId;
    private String username;
    private String userAvatar;
    private String content;
    private long timestamp;
    private int likeCount;
    private Map<String, Boolean> likedBy; // Map userId -> boolean để track ai đã like

    public Comment() {
        // Constructor rỗng cần thiết cho Firebase
        this.likedBy = new HashMap<>();
        this.likeCount = 0;
        this.timestamp = System.currentTimeMillis();
    }

    public Comment(String userId, String username, String userAvatar, String content) {
        this.userId = userId;
        this.username = username;
        this.userAvatar = userAvatar;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.likeCount = 0;
        this.likedBy = new HashMap<>();
    }

    // Getters
    public String getCommentId() {
        return commentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public Map<String, Boolean> getLikedBy() {
        return likedBy;
    }

    // Setters
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setLikedBy(Map<String, Boolean> likedBy) {
        this.likedBy = likedBy;
    }

    // Phương thức để toggle like
    public boolean toggleLike(String userId) {
        if (likedBy == null) {
            likedBy = new HashMap<>();
        }
        
        boolean isLiked = likedBy.containsKey(userId) && likedBy.get(userId);
        if (isLiked) {
            // Unlike
            likedBy.remove(userId);
            likeCount = Math.max(0, likeCount - 1);
        } else {
            // Like
            likedBy.put(userId, true);
            likeCount++;
        }
        return !isLiked; // Trả về trạng thái mới
    }

    // Kiểm tra user đã like chưa
    public boolean isLikedByUser(String userId) {
        return likedBy != null && likedBy.containsKey(userId) && likedBy.get(userId);
    }
} 