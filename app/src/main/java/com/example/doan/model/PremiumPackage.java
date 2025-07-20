package com.example.doan.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PremiumPackage {
    private String id;
    private String name;
    private String description;
    private double price;
    private int duration; // thời hạn tính bằng ngày
    private String createdDate;
    private boolean isActive;

    // Constructor mặc định cho Firebase
    public PremiumPackage() {
        this.createdDate = getCurrentDate();
        this.isActive = true;
    }

    // Constructor đầy đủ
    public PremiumPackage(String name, String description, double price, int duration) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createdDate = getCurrentDate();
        this.isActive = true;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Phương thức helper
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
        return sdf.format(new Date());
    }

    public String getFormattedPrice() {
        return String.format(Locale.getDefault(), "%,.0f VNĐ", price);
    }

    public String getDurationText() {
        if (duration == 1) {
            return "1 ngày";
        } else if (duration == 7) {
            return "1 tuần";
        } else if (duration == 30) {
            return "1 tháng";
        } else if (duration == 365) {
            return "1 năm";
        } else {
            return duration + " ngày";
        }
    }
}
