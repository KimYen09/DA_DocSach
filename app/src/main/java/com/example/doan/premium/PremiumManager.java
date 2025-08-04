package com.example.doan.premium;

import android.content.Context;
import android.util.Log;

import com.example.doan.model.UserPremiumStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PremiumManager {
    private static final String TAG = "PremiumManager";
    private final DatabaseReference userPremiumRef;
    private final FirebaseAuth mAuth;
    private final Context context;

    public PremiumManager(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userPremiumRef = database.getReference("UserPremiumStatus");
    }

    /**
     * Interface callback cho việc kiểm tra trạng thái premium
     */
    public interface PremiumCheckCallback {
        void onResult(boolean isPremium);
        void onError(String error);
    }

    /**
     * Interface callback cho việc cập nhật trạng thái premium
     */
    public interface PremiumUpdateCallback {
        void onSuccess();
        void onError(String error);
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải premium không
     */
    public void checkUserPremiumStatus(PremiumCheckCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            callback.onResult(false);
            return;
        }

        String userId = currentUser.getUid();

        userPremiumRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserPremiumStatus premiumStatus = dataSnapshot.getValue(UserPremiumStatus.class);
                    if (premiumStatus != null) {
                        // Kiểm tra xem gói premium có còn hiệu lực không
                        boolean isValid = isPremiumValid(premiumStatus);
                        callback.onResult(isValid);
                    } else {
                        callback.onResult(false);
                    }
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error checking premium status", databaseError.toException());
                callback.onError("Lỗi kiểm tra trạng thái premium: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Kiểm tra xem gói premium có còn hiệu lực không
     */
    private boolean isPremiumValid(UserPremiumStatus premiumStatus) {
        if (!premiumStatus.isActive()) {
            Log.d(TAG, "Premium is not active");
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date expiryDate = sdf.parse(premiumStatus.getExpiryDate());
            Date currentDate = new Date();

            Log.d(TAG, "Current date: " + sdf.format(currentDate));
            Log.d(TAG, "Expiry date: " + premiumStatus.getExpiryDate());

            boolean isValid = expiryDate != null && expiryDate.after(currentDate);
            Log.d(TAG, "Premium valid: " + isValid);

            return isValid;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing expiry date: " + premiumStatus.getExpiryDate(), e);
            // Nếu có lỗi parse ngày, coi như còn hiệu lực (để tránh chặn nhầm)
            return true;
        }
    }

    /**
     * Cập nhật trạng thái premium cho người dùng sau khi mua gói
     */
    public void updateUserPremiumStatus(String packageId, String packageName,
                                       int durationDays, double paidAmount,
                                       String paymentMethod, PremiumUpdateCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String userId = currentUser.getUid();

        // Tính toán ngày hết hạn
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + (durationDays * 24L * 60 * 60 * 1000));

        String purchaseDate = sdf.format(currentDate);
        String expiryDateStr = sdf.format(expiryDate);

        UserPremiumStatus premiumStatus = new UserPremiumStatus(
            userId, packageId, packageName, purchaseDate,
            expiryDateStr, paidAmount, paymentMethod
        );

        userPremiumRef.child(userId).setValue(premiumStatus)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Premium status updated successfully for user: " + userId);
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to update premium status", e);
                callback.onError("Lỗi cập nhật trạng thái premium: " + e.getMessage());
            });
    }

    /**
     * Kiểm tra xem người dùng có thể truy cập truyện premium không
     */
    public void canAccessPremiumStory(PremiumCheckCallback callback) {
        checkUserPremiumStatus(callback);
    }

    /**
     * Vô hiệu hóa gói premium (khi hết hạn hoặc hủy)
     */
    public void deactivatePremium(PremiumUpdateCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String userId = currentUser.getUid();

        userPremiumRef.child(userId).child("active").setValue(false)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Premium deactivated for user: " + userId);
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to deactivate premium", e);
                callback.onError("Lỗi vô hiệu hóa premium: " + e.getMessage());
            });
    }

    /**
     * Refresh và kiểm tra lại trạng thái premium (dùng sau khi mua gói)
     */
    public void refreshPremiumStatus(PremiumCheckCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            callback.onResult(false);
            return;
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "Refreshing premium status for user: " + userId);
        Log.d(TAG, "Checking path: UserPremiumStatus/" + userId);

        // Force refresh từ server, không dùng cache
        userPremiumRef.child(userId).keepSynced(false);
        userPremiumRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot exists: " + dataSnapshot.exists());
                Log.d(TAG, "DataSnapshot key: " + dataSnapshot.getKey());
                Log.d(TAG, "DataSnapshot value: " + dataSnapshot.getValue());

                if (dataSnapshot.exists()) {
                    UserPremiumStatus premiumStatus = dataSnapshot.getValue(UserPremiumStatus.class);
                    Log.d(TAG, "Premium status object: " + (premiumStatus != null ? "Found" : "Null"));
                    if (premiumStatus != null) {
                        Log.d(TAG, "Package: " + premiumStatus.getPackageName() + ", Active: " + premiumStatus.isActive());
                        Log.d(TAG, "Expiry: " + premiumStatus.getExpiryDate());
                        boolean isValid = isPremiumValid(premiumStatus);
                        callback.onResult(isValid);
                    } else {
                        Log.d(TAG, "Premium status is null");
                        callback.onResult(false);
                    }
                } else {
                    Log.d(TAG, "No premium data found for user");
                    // Debug: Thử kiểm tra tất cả các node có thể có
                    checkAlternativePremiumPaths(userId, callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error refreshing premium status", databaseError.toException());
                callback.onError("Lỗi refresh trạng thái premium: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Kiểm tra các path khác có thể chứa dữ liệu premium
     */
    private void checkAlternativePremiumPaths(String userId, PremiumCheckCallback callback) {
        Log.d(TAG, "Checking alternative premium paths...");

        // Kiểm tra node user_premium_status (với underscore)
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference altRef = database.getReference("user_premium_status").child(userId);

        altRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Alternative path (user_premium_status) exists: " + dataSnapshot.exists());
                Log.d(TAG, "Alternative path value: " + dataSnapshot.getValue());

                if (dataSnapshot.exists()) {
                    UserPremiumStatus premiumStatus = dataSnapshot.getValue(UserPremiumStatus.class);
                    if (premiumStatus != null) {
                        Log.d(TAG, "Found premium data in alternative path!");
                        boolean isValid = isPremiumValid(premiumStatus);
                        callback.onResult(isValid);
                        return;
                    }
                }

                Log.d(TAG, "No premium data found in any path. User is not premium.");
                callback.onResult(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error checking alternative path", databaseError.toException());
                callback.onResult(false);
            }
        });
    }
}
