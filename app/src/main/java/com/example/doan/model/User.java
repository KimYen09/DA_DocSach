package com.example.doan.model;

public class User {
    private String userId; // Đã là userId
    private String username;
    String authorName;
    private String email;
    private String avatar; // Đổi tên thành avatarUrl để nhất quán
    private String joinDate;
    private int storyCount;
    private int likes;
    private String role;
    private String requestStatus;
    private String authorBio; // Đã có authorBio

    public User() {
        // Constructor rỗng cần thiết cho Firebase
    }

    // Constructor đầy đủ (cập nhật để bao gồm tất cả các trường)
    public User(String userId, String username, String email, String avatar, String joinDate, int storyCount, int likes, String role, String requestStatus, String authorBio) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.avatar = avatar; // Sử dụng avatar
        this.joinDate = joinDate;
        this.storyCount = storyCount;
        this.likes = likes;
        this.role = role;
        this.requestStatus = requestStatus;
        this.authorBio = authorBio;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() { // Giữ tên getAvatar để khớp với cách bạn dùng trong adapter/fragment
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

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getAuthorBio() {
        return authorBio; // <-- ĐÃ SỬA: Trả về authorBio
    }

    // Setters
    public void setUserId(String userId) { // <-- THÊM: Phương thức setter cho userId
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthorName(String authorname) {
        this.authorName = authorname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) { // Setter cho avatar
        this.avatar = avatar;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public void setStoryCount(int storyCount) {
        this.storyCount = storyCount;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
    }
}
