package com.example.doan.premium;

public class PurchaseRecord {
    private String purchaseId;
    private String packageName;
    private String packagePrice;
    private String purchaseDate; // Formatted date string
    private long timestamp;      // For sorting

    // Required empty constructor for Firebase
    public PurchaseRecord() {
    }

    public PurchaseRecord(String purchaseId, String packageName, String packagePrice, String purchaseDate, long timestamp) {
        this.purchaseId = purchaseId;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.purchaseDate = purchaseDate;
        this.timestamp = timestamp;
    }

    // --- Getters and Setters for all fields ---
    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

