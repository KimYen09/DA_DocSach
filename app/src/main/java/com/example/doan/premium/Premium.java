package com.example.doan.premium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.model.UserPremiumStatus;
import com.example.doan.utils.PremiumManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Premium extends AppCompatActivity {
    private ImageView ivexit;
    private Button pre1m, pre6m, pre1y, btnLichSuMuaHang;
    private TextView tvPremiumStatus; // Thêm TextView để hiển thị trạng thái Premium

    private PremiumManager premiumManager;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private static final String goi_1_thang = "Gói 1 Tháng";
    private static final String phi_1_thang = "VND 39,000";
    private static final int duration_1_thang = 30;

    private static final String goi_6_thang = "Gói 6 Tháng";
    private static final String phi_6_thang = "VND 199,000";
    private static final int duration_6_thang = 180;

    private static final String goi_1_nam = "Gói 1 Năm";
    private static final String phi_1_nam = "VND 399,000";
    private static final int duration_1_nam = 365;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        // Khởi tạo Firebase Auth và PremiumManager
        mAuth = FirebaseAuth.getInstance();
        premiumManager = new PremiumManager();

        // Lấy user ID hiện tại
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        initViews();
        setupClickListeners();
        checkCurrentPremiumStatus();
    }

    private void initViews() {
        ivexit = findViewById(R.id.ivexit);
        pre1m = findViewById(R.id.pre1m);
        pre6m = findViewById(R.id.pre6m);
        pre1y = findViewById(R.id.pre1y);
        btnLichSuMuaHang = findViewById(R.id.btnLichSuMuaHang);
        tvPremiumStatus = findViewById(R.id.tvPremiumStatus); // Cần thêm vào layout
    }

    private void setupClickListeners() {
        // Nút premium 1 tháng
        pre1m.setOnClickListener(v -> checkAndPurchase(goi_1_thang, phi_1_thang, duration_1_thang, 39000));

        // Nút premium 6 tháng
        pre6m.setOnClickListener(v -> checkAndPurchase(goi_6_thang, phi_6_thang, duration_6_thang, 199000));

        // Nút premium 1 năm
        pre1y.setOnClickListener(v -> checkAndPurchase(goi_1_nam, phi_1_nam, duration_1_nam, 399000));

        // Nút lịch sử mua hàng
        btnLichSuMuaHang.setOnClickListener(v -> openPurchaseHistory());

        // Nút exit
        ivexit.setOnClickListener(v -> finish());
    }

    private void checkCurrentPremiumStatus() {
        if (currentUserId == null) {
            updatePremiumStatusUI(false, null);
            return;
        }

        premiumManager.checkUserPremiumStatus(currentUserId, new PremiumManager.PremiumStatusCallback() {
            @Override
            public void onPremiumStatusChecked(boolean isActive, UserPremiumStatus status) {
                runOnUiThread(() -> updatePremiumStatusUI(isActive, status));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(Premium.this, error, Toast.LENGTH_SHORT).show();
                    updatePremiumStatusUI(false, null);
                });
            }
        });
    }

    private void updatePremiumStatusUI(boolean isActive, UserPremiumStatus status) {
        if (tvPremiumStatus != null) {
            if (isActive && status != null) {
                // User có Premium đang hoạt động
                tvPremiumStatus.setText("Trạng thái: " + status.getStatusText() +
                                      "\nGói: " + status.getPackageName() +
                                      "\nHết hạn: " + status.getExpiryDate());
                tvPremiumStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                // Disable các nút mua Premium
                pre1m.setEnabled(false);
                pre6m.setEnabled(false);
                pre1y.setEnabled(false);

                pre1m.setText("Đã có Premium");
                pre6m.setText("Đã có Premium");
                pre1y.setText("Đã có Premium");

            } else if (status != null && status.isExpired()) {
                // User đã có Premium nhưng hết hạn
                tvPremiumStatus.setText("Trạng thái: Đã hết hạn" +
                                      "\nGói cũ: " + status.getPackageName() +
                                      "\nHết hạn: " + status.getExpiryDate());
                tvPremiumStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                // Enable lại các nút mua Premium
                enablePurchaseButtons();

            } else {
                // User chưa có Premium
                tvPremiumStatus.setText("Trạng thái: Chưa có gói Premium\nMua gói để trải nghiệm đầy đủ tính năng!");
                tvPremiumStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));

                // Enable các nút mua Premium
                enablePurchaseButtons();
            }
        }
    }

    private void enablePurchaseButtons() {
        pre1m.setEnabled(true);
        pre6m.setEnabled(true);
        pre1y.setEnabled(true);

        pre1m.setText("1 Tháng - " + phi_1_thang);
        pre6m.setText("6 Tháng - " + phi_6_thang);
        pre1y.setText("1 Năm - " + phi_1_nam);
    }

    private void checkAndPurchase(String packageName, String price, int durationDays, double amount) {
        if (currentUserId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua gói Premium", Toast.LENGTH_LONG).show();
            return;
        }

        // Kiểm tra trạng thái Premium trước khi cho phép mua
        premiumManager.checkUserPremiumStatus(currentUserId, new PremiumManager.PremiumStatusCallback() {
            @Override
            public void onPremiumStatusChecked(boolean isActive, UserPremiumStatus status) {
                runOnUiThread(() -> {
                    if (isActive && status != null && !status.isExpired()) {
                        // User đã có Premium đang hoạt động
                        showPremiumActiveDialog(status);
                    } else {
                        // User có thể mua Premium
                        showPurchaseConfirmationDialog(packageName, price, durationDays, amount);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(Premium.this, error, Toast.LENGTH_SHORT).show());
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
                           "Bạn chỉ có thể mua gói mới sau khi gói hiện tại hết hạn.")
                .setPositiveButton("Đã hiểu", null)
                .setNeutralButton("Xem lịch sử", (dialog, which) -> openPurchaseHistory())
                .show();
    }

    private void showPurchaseConfirmationDialog(String packageName, String price, int durationDays, double amount) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận mua gói Premium")
                .setMessage("Bạn có chắc chắn muốn mua:\n\n" +
                           "Gói: " + packageName + "\n" +
                           "Giá: " + price + "\n" +
                           "Thời hạn: " + durationDays + " ngày\n\n" +
                           "Sau khi mua, bạn sẽ không thể mua gói khác cho đến khi hết hạn.")
                .setPositiveButton("Mua ngay", (dialog, which) -> {
                    navigateToPayment(packageName, price, durationDays, amount);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void navigateToPayment(String packageName, String price, int durationDays, double amount) {
        Intent intent = new Intent(Premium.this, Payment.class);
        intent.putExtra("ten_goi", packageName);
        intent.putExtra("phi_goi", price);
        intent.putExtra("duration_days", durationDays);
        intent.putExtra("amount", amount);
        startActivity(intent);
    }

    private void openPurchaseHistory() {
        if (currentUserId != null) {
            Intent intent = new Intent(Premium.this, LichSuMuaHangActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử mua hàng", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh trạng thái Premium khi quay lại từ màn hình khác
        checkCurrentPremiumStatus();
    }
}

