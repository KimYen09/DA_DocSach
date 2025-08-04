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
     * Lưu thông tin mua gói vào Firebase và cập nhật trạng thái premium
     * @param packageName Tên gói đã mua
     * @param packagePrice Giá gói
     * @param packageId ID gói premium
     * @param durationDays Thời hạn gói tính bằng ngày
     * @param paymentMethod Phương thức thanh toán
     * @param onComplete Callback khi hoàn thành
     */
    public void savePurchaseRecordAndUpdatePremium(String packageName, String packagePrice,
                                                  String packageId, int durationDays,
                                                  String paymentMethod, PremiumPurchaseCallback onComplete) {
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
        double price = parsePrice(packagePrice);

        Log.d(TAG, "Attempting to save purchase and update premium for user: " + userId);

        // Tạo đối tượng PurchaseRecord
        PurchaseRecord purchaseRecord = new PurchaseRecord(
            purchaseId,
            packageName,
            packagePrice,
            formattedDate,
            timestamp
        );

        // Lưu lịch sử mua hàng
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userPurchaseRef = database.getReference("giaoDich").child(userId).child(purchaseId);

        userPurchaseRef.setValue(purchaseRecord)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Purchase record saved successfully");

                // Cập nhật trạng thái premium
                PremiumManager premiumManager = new PremiumManager(context);
                premiumManager.updateUserPremiumStatus(packageId, packageName, durationDays,
                                                     price, paymentMethod,
                                                     new PremiumManager.PremiumUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Premium status updated successfully");

                        // Delay một chút để đảm bảo Firebase đã sync
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            // Refresh lại trạng thái premium để đảm bảo đã cập nhật
                            premiumManager.refreshPremiumStatus(new PremiumManager.PremiumCheckCallback() {
                                @Override
                                public void onResult(boolean isPremium) {
                                    if (isPremium) {
                                        Toast.makeText(context, "🎉 Mua gói thành công! Bạn đã trở thành thành viên Premium", Toast.LENGTH_LONG).show();
                                        if (onComplete != null) {
                                            onComplete.onSuccess(purchaseRecord);
                                        }
                                    } else {
                                        Toast.makeText(context, "⚠️ Đã mua gói nhưng chưa kích hoạt. Vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                                        if (onComplete != null) {
                                            onComplete.onPartialSuccess(purchaseRecord, "Premium chưa được kích hoạt");
                                        }
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(context, "Đã mua gói thành công! Nếu vẫn chưa truy cập được, vui lòng khởi động lại ứng dụng.", Toast.LENGTH_LONG).show();
                                    if (onComplete != null) {
                                        onComplete.onSuccess(purchaseRecord);
                                    }
                                }
                            });
                        }, 2000); // Delay 2 giây
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Failed to update premium status: " + error);
                        Toast.makeText(context, "Đã lưu giao dịch nhưng lỗi cập nhật premium: " + error, Toast.LENGTH_LONG).show();
                        if (onComplete != null) {
                            onComplete.onPartialSuccess(purchaseRecord, error);
                        }
                    }
                });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to save purchase record", e);
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
     * Parse giá từ string sang double
     */
    private double parsePrice(String priceString) {
        try {
            // Loại bỏ ký tự không phải số và dấu thập phân
            String cleanPrice = priceString.replaceAll("[^\\d.]", "");
            return Double.parseDouble(cleanPrice);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Failed to parse price: " + priceString, e);
            return 0.0;
        }
    }

    /**
     * Interface callback cho việc lưu dữ liệu
     */
    public interface PurchaseCallback {
        void onSuccess(PurchaseRecord purchaseRecord);
        void onFailure(String error);
    }

    /**
     * Interface callback mở rộng cho việc lưu dữ liệu và cập nhật premium
     */
    public interface PremiumPurchaseCallback {
        void onSuccess(PurchaseRecord purchaseRecord);
        void onPartialSuccess(PurchaseRecord purchaseRecord, String premiumError);
        void onFailure(String error);
    }
}
