package com.example.doan.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.doan.model.UserPremiumStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PremiumManager {
    private static final String TAG = "PremiumManager";
    private DatabaseReference databaseReference;

    public interface PremiumStatusCallback {
        void onPremiumStatusChecked(boolean isActive, UserPremiumStatus status);
        void onError(String error);
    }

    public interface PremiumPurchaseCallback {
        void onPurchaseSuccess(String message);
        void onPurchaseError(String error);
    }

    public PremiumManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference("user_premium_status");
    }

    // Kiểm tra trạng thái Premium của user
    public void checkUserPremiumStatus(String userId, PremiumStatusCallback callback) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserPremiumStatus status = snapshot.getValue(UserPremiumStatus.class);
                    if (status != null) {
                        // Kiểm tra xem gói Premium có còn hiệu lực không
                        boolean isActive = status.isActive() && !status.isExpired();
                        callback.onPremiumStatusChecked(isActive, status);
                    } else {
                        callback.onPremiumStatusChecked(false, null);
                    }
                } else {
                    // User chưa mua Premium bao giờ
                    callback.onPremiumStatusChecked(false, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking premium status: " + error.getMessage());
                callback.onError("Lỗi khi kiểm tra trạng thái Premium: " + error.getMessage());
            }
        });
    }

    // Mua gói Premium
    public void purchasePremium(String userId, String packageId, String packageName,
                              int durationDays, double amount, String paymentMethod,
                              PremiumPurchaseCallback callback) {

        // Trước tiên kiểm tra xem user có Premium đang hoạt động không
        checkUserPremiumStatus(userId, new PremiumStatusCallback() {
            @Override
            public void onPremiumStatusChecked(boolean isActive, UserPremiumStatus currentStatus) {
                if (isActive && currentStatus != null && !currentStatus.isExpired()) {
                    // User đã có Premium đang hoạt động
                    long daysRemaining = currentStatus.getDaysRemaining();
                    callback.onPurchaseError("Bạn đã có gói Premium đang hoạt động. " +
                            "Còn lại " + daysRemaining + " ngày. " +
                            "Vui lòng chờ hết hạn để mua gói mới.");
                    return;
                }

                // User có thể mua Premium
                String purchaseDate = getCurrentDate();
                String expiryDate = getExpiryDate(durationDays);

                UserPremiumStatus newStatus = new UserPremiumStatus(
                    userId, packageId, packageName, purchaseDate, expiryDate, amount, paymentMethod
                );

                // Lưu vào Firebase
                databaseReference.child(userId).setValue(newStatus)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Premium purchased successfully for user: " + userId);
                        callback.onPurchaseSuccess("Mua gói Premium thành công! " +
                                "Gói sẽ hết hạn vào " + expiryDate);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error purchasing premium: " + e.getMessage());
                        callback.onPurchaseError("Lỗi khi mua Premium: " + e.getMessage());
                    });
            }

            @Override
            public void onError(String error) {
                callback.onPurchaseError(error);
            }
        });
    }

    // Hủy gói Premium (nếu cần)
    public void cancelPremium(String userId, PremiumPurchaseCallback callback) {
        databaseReference.child(userId).child("active").setValue(false)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Premium cancelled for user: " + userId);
                callback.onPurchaseSuccess("Đã hủy gói Premium thành công");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error cancelling premium: " + e.getMessage());
                callback.onPurchaseError("Lỗi khi hủy Premium: " + e.getMessage());
            });
    }

    // Utility methods
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
        return sdf.format(new Date());
    }

    private String getExpiryDate(int durationDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, durationDays);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
        return sdf.format(calendar.getTime());
    }

    // Kiểm tra xem user có quyền truy cập Premium content không
    public static boolean canAccessPremiumContent(UserPremiumStatus status) {
        if (status == null) return false;
        return status.isActive() && !status.isExpired();
    }
}
