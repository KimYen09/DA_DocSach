package com.example.doan.premium;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PurchaseManager {

    private static final String TAG = "PurchaseManager";
    private final DatabaseReference purchaseHistoryRef;
    private final FirebaseAuth mAuth;
    private final Context context;

    public PurchaseManager(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        // Sử dụng đúng URL từ google-services.json
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.purchaseHistoryRef = database.getReference("PurchaseHistory");
    }

    /**
     * Lưu thông tin mua gói vào Firebase (sử dụng node giaoDich thay vì PurchaseHistory)
     * @param packageName Tên gói đã mua
     * @param packagePrice Giá gói
     * @param onComplete Callback khi hoàn thành
     */
    public void savePurchaseRecord(String packageName, String packagePrice, PurchaseCallback onComplete) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            String error = "Người dùng chưa đăng nhập";
            Log.e(TAG, error);
            if (onComplete != null) {
                onComplete.onFailure(error);
            }
            return;
        }

        String userId = currentUser.getUid();
        String purchaseId = generatePurchaseId();
        long timestamp = System.currentTimeMillis();
        String formattedDate = formatDate(timestamp);

        Log.d(TAG, "Attempting to save purchase for user: " + userId);
        Log.d(TAG, "Purchase ID: " + purchaseId);
        Log.d(TAG, "Package: " + packageName + " - Price: " + packagePrice);

        // Tạo đối tượng PurchaseRecord
        PurchaseRecord purchaseRecord = new PurchaseRecord(
            purchaseId,
            packageName,
            packagePrice,
            formattedDate,
            timestamp
        );

        // Sử dụng node giaoDich thay vì PurchaseHistory để phù hợp với rules hiện tại
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userPurchaseRef = database.getReference("giaoDich").child(userId).child(purchaseId);

        userPurchaseRef.setValue(purchaseRecord)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Purchase record saved successfully");
                Log.d(TAG, "Saved to path: giaoDich/" + userId + "/" + purchaseId);
                Toast.makeText(context, "Đã lưu lịch sử mua hàng", Toast.LENGTH_SHORT).show();
                if (onComplete != null) {
                    onComplete.onSuccess(purchaseRecord);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to save purchase record", e);
                Log.e(TAG, "Error details: " + e.getMessage());
                Toast.makeText(context, "Lỗi lưu lịch sử: " + e.getMessage(), Toast.LENGTH_LONG).show();
                if (onComplete != null) {
                    onComplete.onFailure("Lỗi lưu dữ liệu: " + e.getMessage());
                }
            });
    }

    /**
     * Tạo ID giao dịch duy nhất
     */
    private String generatePurchaseId() {
        return "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Format timestamp thành chuỗi ngày giờ dễ đọc
     */
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Interface callback cho việc lưu dữ liệu
     */
    public interface PurchaseCallback {
        void onSuccess(PurchaseRecord purchaseRecord);
        void onFailure(String error);
    }
}
