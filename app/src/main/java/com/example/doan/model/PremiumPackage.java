package com.example.doan.model;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PremiumPackage {
    private String id;
    private String tenGoi; // Tên gói - Firebase sẽ map thành "tenGoi"
    private double gia;    // Giá - Firebase sẽ map thành "gia"
    private int ngaySD;    // Số ngày sử dụng - Firebase sẽ map thành "ngaySD"

    // Các field bổ sung (không bắt buộc trên Firebase)
    private String description;
    private String createdDate;
    private boolean isActive;
    private String features;

    // Constructor không tham số (cần thiết cho Firebase)
    public PremiumPackage() {
        this.isActive = true;
        this.createdDate = getCurrentDateString();
    }

    // Constructor với các field Firebase bắt buộc
    public PremiumPackage(String tenGoi, double gia, int ngaySD) {
        this.tenGoi = tenGoi;
        this.gia = gia;
        this.ngaySD = ngaySD;
        this.isActive = true;
        this.createdDate = getCurrentDateString();
    }

    // Constructor đầy đủ
    public PremiumPackage(String tenGoi, double gia, int ngaySD, String description, String features) {
        this.tenGoi = tenGoi;
        this.gia = gia;
        this.ngaySD = ngaySD;
        this.description = description;
        this.features = features;
        this.isActive = true;
        this.createdDate = getCurrentDateString();
    }

    // Getters và Setters cho Firebase fields (PHẢI có để Firebase mapping)
    public String getTenGoi() {
        return tenGoi;
    }

    public void setTenGoi(String tenGoi) {
        this.tenGoi = tenGoi;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public int getNgaySD() {
        return ngaySD;
    }

    public void setNgaySD(int ngaySD) {
        this.ngaySD = ngaySD;
    }

    // Getters và Setters cho các field bổ sung
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    // Methods tương thích với code cũ (để không bị lỗi)
    public String getName() {
        return tenGoi != null ? tenGoi : "";
    }

    public void setName(String name) {
        this.tenGoi = name;
    }

    public double getPrice() {
        return gia;
    }

    public void setPrice(double price) {
        this.gia = price;
    }

    public int getDuration() {
        return ngaySD;
    }

    public void setDuration(int duration) {
        this.ngaySD = duration;
    }

    // Phương thức tiện ích
    public String getFormattedPrice() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(gia);
    }

    public String getDurationText() {
        if (ngaySD == 30) {
            return "1 tháng";
        } else if (ngaySD == 90) {
            return "3 tháng";
        } else if (ngaySD == 365) {
            return "1 năm";
        } else {
            return ngaySD + " ngày";
        }
    }

    // Thay đổi getFeaturesArray() thành getFeaturesList() và trả về List
    public java.util.List<String> getFeaturesList() {
        if (features == null || features.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return java.util.Arrays.asList(features.split(","));
    }

    // Method để lấy ngày hiện tại
    private String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Override toString để debug
    @Override
    public String toString() {
        return "PremiumPackage{" +
                "id='" + id + '\'' +
                ", tenGoi='" + tenGoi + '\'' +
                ", description='" + description + '\'' +
                ", gia=" + gia +
                ", ngaySD=" + ngaySD +
                ", createdDate='" + createdDate + '\'' +
                ", isActive=" + isActive +
                ", features='" + features + '\'' +
                '}';
    }
}
