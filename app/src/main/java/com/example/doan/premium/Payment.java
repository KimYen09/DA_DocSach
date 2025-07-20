package com.example.doan.premium;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.doan.R;
import com.example.doan.fragmenthome.HomeFragment;
import com.example.doan.model.UserPremiumStatus;
import com.example.doan.utils.PremiumManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Payment extends AppCompatActivity {
    private TextView tvTenGoi, tvPhigoi, tvThongtingoi;
    private Button btnXacNhan, btnHuy;

    private String goiDuocChon;
    private String phiCuaGoi;
    private int durationDays;
    private double amount;

    private DatabaseReference LichSuMuaHang;
    private FirebaseAuth mAuth;
    private PremiumManager premiumManager;
    private PurchaseManager purchaseManager;

    public static final String PREMIUM_PREFS_NAME = "PremiumPrefs";
    public static final String KEY_IS_USER_PREMIUM = "isUserPremium";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        initManagers();
        getIntentData();
        setupClickListeners();

        // Kiểm tra trạng thái Premium trước khi cho phép thanh toán
        checkPremiumStatusBeforePayment();
    }

    private void initViews() {
        tvTenGoi = findViewById(R.id.tvTenGoi);
        tvPhigoi = findViewById(R.id.tvPhigoi);
        tvThongtingoi = findViewById(R.id.tvThongtingoi);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnHuy = findViewById(R.id.btnHuy);
    }

    private void initManagers() {
        mAuth = FirebaseAuth.getInstance();
        premiumManager = new PremiumManager();
        purchaseManager = new PurchaseManager(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        goiDuocChon = intent.getStringExtra("ten_goi");
        phiCuaGoi = intent.getStringExtra("phi_goi");
        durationDays = intent.getIntExtra("duration_days", 30); // mặc định 30 ngày
        amount = intent.getDoubleExtra("amount", 0.0);

        if (goiDuocChon != null) {
            tvTenGoi.setText("Gói đã chọn: " + goiDuocChon);
        }
        if (phiCuaGoi != null) {
            tvPhigoi.setText("Giá: " + phiCuaGoi);
        }

        tvThongtingoi.setText("Ngày thanh toán: " + getCurrentDate() +
                             "\nThời hạn: " + durationDays + " ngày");
    }

    private void setupClickListeners() {
        btnXacNhan.setOnClickListener(v -> processPurchase());
        btnHuy.setOnClickListener(v -> {
            Toast.makeText(Payment.this, "Thanh toán đã hủy.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void checkPremiumStatusBeforePayment() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showError("Vui lòng đăng nhập để mua gói Premium");
            return;
        }

        // Kiểm tra trạng thái Premium hiện tại
        premiumManager.checkUserPremiumStatus(currentUser.getUid(), new PremiumManager.PremiumStatusCallback() {
            @Override
            public void onPremiumStatusChecked(boolean isActive, UserPremiumStatus status) {
                runOnUiThread(() -> {
                    if (isActive && status != null && !status.isExpired()) {
                        // User đã có Premium đang hoạt động - không cho phép mua
                        showPremiumActiveDialog(status);
                    } else {
                        // User có thể mua Premium - enable nút thanh toán
                        btnXacNhan.setEnabled(true);
                        btnXacNhan.setText("Xác nhận thanh toán");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("Payment", "Error checking premium status: " + error);
                    // Nếu có lỗi kiểm tra, vẫn cho phép mua (fallback)
                    btnXacNhan.setEnabled(true);
                    btnXacNhan.setText("Xác nhận thanh toán");
                });
            }
        });
    }

    private void showPremiumActiveDialog(UserPremiumStatus status) {
        new AlertDialog.Builder(this)
                .setTitle("Đã có gói Premium")
                .setMessage("Bạn đã có gói Premium đang hoạt động:\n\n" +
                           "Gói: " + status.getPackageName() + "\n" +
                           "Trạng thái: " + status.getStatusText() + "\n" +
                           "Hết hạn: " + status.getExpiryDate() + "\n\n" +
                           "Bạn không thể mua gói mới khi gói hiện tại còn hiệu lực.")
                .setPositiveButton("Đã hiểu", (dialog, which) -> finish())
                .setNeutralButton("Xem lịch sử", (dialog, which) -> {
                    Intent intent = new Intent(Payment.this, LichSuMuaHangActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();

        // Disable nút thanh toán
        btnXacNhan.setEnabled(false);
        btnXacNhan.setText("Không thể mua - Đã có Premium");
    }

    private void processPurchase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showError("Vui lòng đăng nhập để mua gói Premium");
            return;
        }

        // Disable nút để tránh double-click
        btnXacNhan.setEnabled(false);
        btnXacNhan.setText("Đang xử lý...");

        Toast.makeText(this, "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();

        // Sử dụng PremiumManager để mua Premium (đã có logic kiểm tra ràng buộc)
        premiumManager.purchasePremium(
            currentUser.getUid(),
            "premium_" + System.currentTimeMillis(), // package ID
            goiDuocChon,
            durationDays,
            amount,
            "In-App Purchase",
            new PremiumManager.PremiumPurchaseCallback() {
                @Override
                public void onPurchaseSuccess(String message) {
                    runOnUiThread(() -> {
                        Log.d("Payment", "Premium purchase successful");

                        // Lưu vào SharedPreferences để tương thích với code cũ
                        saveUserAsPremium(true);

                        // Lưu lịch sử mua hàng
                        savePurchaseHistory();

                        Toast.makeText(Payment.this,
                            "Thanh toán thành công!\n" + message,
                            Toast.LENGTH_LONG).show();

                        // Chuyển về trang Home sau 2 giây
                        new android.os.Handler().postDelayed(() -> navigateToHome(), 2000);
                    });
                }

                @Override
                public void onPurchaseError(String error) {
                    runOnUiThread(() -> {
                        Log.e("Payment", "Premium purchase failed: " + error);

                        // Re-enable nút thanh toán
                        btnXacNhan.setEnabled(true);
                        btnXacNhan.setText("Xác nhận thanh toán");

                        showError("Lỗi thanh toán: " + error);
                    });
                }
            }
        );
    }

    private void savePurchaseHistory() {
        purchaseManager.savePurchaseRecord(goiDuocChon, phiCuaGoi,
            new PurchaseManager.PurchaseCallback() {
                @Override
                public void onSuccess(PurchaseRecord purchaseRecord) {
                    Log.d("Payment", "Purchase history saved: " + purchaseRecord.getPurchaseId());
                }

                @Override
                public void onFailure(String error) {
                    Log.e("Payment", "Failed to save purchase history: " + error);
                }
            });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(Payment.this, HomeFragment.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.putExtra("NAVIGATE_TO_HOME_AFTER_PAYMENT", true);
        startActivity(homeIntent);
        finishAffinity();
    }

    private void saveUserAsPremium(boolean isPremium) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREMIUM_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_USER_PREMIUM, isPremium);
        editor.apply();
        Log.d("Payment", "User premium status saved to SharedPreferences: " + isPremium);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
        return sdf.format(new Date());
    }
}
