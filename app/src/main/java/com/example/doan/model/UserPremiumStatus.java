package com.example.doan.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserPremiumStatus {
    private String userId;
    private String packageId;
    private String packageName;
    private boolean isActive;
    private String purchaseDate;
    private String expiryDate;
    private double paidAmount;
    private String paymentMethod;

    // Constructor mặc định cho Firebase
    public UserPremiumStatus() {
    }

    // Constructor đầy đủ
    public UserPremiumStatus(String userId, String packageId, String packageName,
                           String purchaseDate, String expiryDate, double paidAmount, String paymentMethod) {
        this.userId = userId;
        this.packageId = packageId;
        this.packageName = packageName;
        this.isActive = true;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Utility methods
    public boolean isExpired() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            Date expiry = sdf.parse(expiryDate);
            Date now = new Date();
            return now.after(expiry);
        } catch (Exception e) {
            return true; // Nếu có lỗi parse date thì coi như đã hết hạn
        }
    }

    public long getDaysRemaining() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            Date expiry = sdf.parse(expiryDate);
            Date now = new Date();
            long diffInMillies = expiry.getTime() - now.getTime();
            return diffInMillies / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getStatusText() {
        if (!isActive) {
            return "Đã hủy";
        } else if (isExpired()) {
            return "Đã hết hạn";
        } else {
            long days = getDaysRemaining();
            if (days <= 0) {
                return "Hết hạn hôm nay";
            } else if (days == 1) {
                return "Còn 1 ngày";
            } else {
                return "Còn " + days + " ngày";
            }
        }
    }
}
