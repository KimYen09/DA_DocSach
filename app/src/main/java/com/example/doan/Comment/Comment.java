package com.example.doan.Comment;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String id;
    private String userId;
    private String avatarUrl;
    private String userName;
    private String content;
    private String time;
    private int likeCount;
    private boolean isLiked;
    private String parentId; // ID của comment cha (null nếu là comment gốc)
    private List<Comment> replies; // Danh sách các reply

    public Comment() {
        // Bắt buộc cho Firebase
    }

    public Comment(String avatarUrl, String userName, String content) {
        this.id = String.valueOf(System.currentTimeMillis()); // Tạo ID đơn giản
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.content = content;
        this.time = "Vừa xong";
        this.likeCount = 0;
        this.isLiked = false;
        this.parentId = null; // Comment gốc
        this.replies = new ArrayList<>();
    }

    public Comment(String avatarUrl, String userName, String content, String time, int likeCount) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.content = content;
        this.time = time;
        this.likeCount = likeCount;
        this.isLiked = false;
        this.parentId = null;
        this.replies = new ArrayList<>();
    }

    // Constructor cho reply
    public Comment(String avatarUrl, String userName, String content, String parentId) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.content = content;
        this.time = "Vừa xong";
        this.likeCount = 0;
        this.isLiked = false;
        this.parentId = parentId;
        this.replies = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public int getLikeCount() { return likeCount; }
    public boolean isLiked() { return isLiked; }
    public String getParentId() { return parentId; }
    public List<Comment> getReplies() { 
        if (replies == null) {
            replies = new ArrayList<>(); // Khởi tạo nếu null để tránh crash
        }
        return replies; 
    }
    public boolean isReply() { return parentId != null; }
    
    public void setLiked(boolean liked) { this.isLiked = liked; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void addReply(Comment reply) { this.replies.add(reply); }
    public void setId(String id) { this.id = id; }
} 