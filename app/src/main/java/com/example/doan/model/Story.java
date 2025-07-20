//package com.example.doan.model;
//
//import com.example.doan.R;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Story {
//    private String id;
//    private String title;
//    private String description;
//    private String category;
//    private String type;
//    private String imageResource;
//    private String authorId;
//    String creationDate;
//    private Map<String, Chapter> chapters;
//
//    public Story(String id, String title, String description, String category,
//                 String imageResource, String type, String authorId, Map<String, Chapter> chapters, String creationDate) {
//        this.id = id;
//        this.title = title;
//        this.description = description;
//        this.category = category;
//        this.imageResource = imageResource;
//        this.type = type;
//        this.authorId = authorId;
//        this.chapters = chapters != null ? chapters : new HashMap<>();
//        this.creationDate = creationDate;
//    }
//
//    public Story(String id, String title, String description, String category, String imageName, String userId, String type) {
//        this.id = id;
//        this.title = title;
//        this.description = description;
//        this.category = category;
//        this.imageResource = imageName;
//        this.authorId = userId;
//        this.type = type;
//    }
//    public Story() {}
//
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public String getImageResource() {
//        return imageResource;
//    }
//
//    public String getAuthorId() {
//        return authorId;
//    }
//
//
//
//    public Map<String, Chapter> getChapters() {
//        return chapters != null ? chapters : new HashMap<>();
//    }
//
//    public String getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(String creationDate) {
//        this.creationDate = creationDate;
//    }
//}

package com.example.doan.model;

import java.util.HashMap;
import java.util.Map; // Thêm import này

public class Story {
    private String id;
    private String title;
    private String description;
    private String category;
    private String imageResource;
    private String userId;
    private String type;
    private HashMap<String, Object> chapters; // <-- THAY ĐỔI TỪ Boolean SANG Object
    private String creationDate;
    private long viewCount;

    // Constructor mặc định (cần thiết cho Firebase)
    public Story() {
    }

    // Constructor có tất cả các tham số (cập nhật để bao gồm creationDate và viewCount)
    public Story(String id, String title, String description, String category, String imageResource, String type, String userId, HashMap<String, Object> chapters, String creationDate, long viewCount) {
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
    }

    // Constructor cho các trường ban đầu (nếu cần, có thể không cần thiết nếu dùng constructor đầy đủ)
    public Story(String id, String title, String description, String category, String imageResource, String userId, String type, HashMap<String, Object> chapters) { // <-- THAY ĐỔI TỪ Boolean SANG Object
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.userId = userId;
        this.type = type;
        this.chapters = chapters;
        this.creationDate = ""; // Khởi tạo mặc định
        this.viewCount = 0; // Khởi tạo mặc định
    }

    // Constructor bạn đang cố gắng gọi từ AddStory/EditStory (9 tham số)
    public Story(String id, String title, String description, String category, String imageResource, String type, String userId, HashMap<String, Object> chapters, String creationDate) { // <-- THAY ĐỔI TỪ Boolean SANG Object
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.type = type;
        this.userId = userId;
        this.chapters = chapters;
        this.creationDate = creationDate;
        this.viewCount = 0; // Khởi tạo mặc định lượt đọc khi tạo mới
    }


    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getChapters() { // <-- THAY ĐỔI TỪ Boolean SANG Object
        return chapters;
    }

    public void setChapters(HashMap<String, Object> chapters) { // <-- THAY ĐỔI TỪ Boolean SANG Object
        this.chapters = chapters;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}
