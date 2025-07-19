package com.example.doan.premium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Premium extends AppCompatActivity {
    private ImageView ivexit;
    private Button pre1m, pre6m, pre1y, btnLichSuMuaHang; // Thêm nút lịch sử mua hàng
    private static final String goi_1_thang = "Gói 1 Tháng";
    private static final String phi_1_thang = "VND 39,000";

    private static final String goi_6_thang = "Gói 6 Tháng";
    private static final String phi_6_thang = "VND 199,000";

    private static final String goi_1_nam = "Gói 1 Năm";
    private static final String phi_1_nam = "VND 399,000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        ivexit = findViewById(R.id.ivexit);
        pre1m = findViewById(R.id.pre1m);
        pre6m = findViewById(R.id.pre6m);
        pre1y = findViewById(R.id.pre1y);
        btnLichSuMuaHang = findViewById(R.id.btnLichSuMuaHang); // Khởi tạo nút lịch sử

        // Nút premium 1 tháng
        pre1m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_1_thang, phi_1_thang);
            }
        });

        // Nút premium 6 tháng
        pre6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_6_thang, phi_6_thang);
            }
        });

        // Nút premium 1 năm
        pre1y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_1_nam, phi_1_nam);
            }
        });

        // Nút lịch sử mua hàng
        btnLichSuMuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPurchaseHistory();
            }
        });

        // Nút test Firebase (tạm thời để debug - có thể xóa sau)
        btnLichSuMuaHang.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Long press nút lịch sử để test Firebase
                FirebaseTestHelper.checkGooglePlayServices(Premium.this);
                return true;
            }
        });

        // Nút lịch sử mua hàng - sửa lại double tap detection
        btnLichSuMuaHang.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;
            private int clickCount = 0;

            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastClickTime < 800) { // Tăng thời gian từ 500ms lên 800ms
                    clickCount++;
                    if (clickCount >= 2) {
                        // Double tap detected - test Firebase
                        Toast.makeText(Premium.this, "🔍 Double tap detected! Testing Firebase...", Toast.LENGTH_SHORT).show();
                        FirebaseTestHelper.testFirebaseConnection(Premium.this);
                        clickCount = 0; // Reset counter
                    }
                } else {
                    clickCount = 1; // Reset counter for new click sequence
                    // Delay để chờ double tap
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (clickCount == 1) {
                                // Single tap - open purchase history
                                openPurchaseHistory();
                            }
                        }
                    }, 400); // Đợi 400ms để xem có click thứ 2 không
                }
                lastClickTime = currentTime;
            }
        });

        // Nút exit
        ivexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Thêm gesture test cho exit button
        ivexit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Long press exit button để debug package info
                FirebaseTestHelper.debugPackageInfo(Premium.this);
                return true;
            }
        });

        ivexit.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 500) {
                    // Double tap exit = test Firebase Auth specifically
                    FirebaseTestHelper.testFirebaseAuth(Premium.this);
                } else {
                    // Single tap = close app
                    finish();
                }
                lastClickTime = currentTime;
            }
        });
    }

    private void navigateToConfirmation(String ten_goi, String phi_goi) {
        Intent intent = new Intent(Premium.this, Payment.class);
        intent.putExtra("ten_goi", ten_goi);
        intent.putExtra("phi_goi", phi_goi);
        startActivity(intent);
    }

    /**
     * Mở màn hình lịch sử mua hàng
     */
    private void openPurchaseHistory() {
        // Kiểm tra user đã đăng nhập chưa
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(Premium.this, LichSuMuaHangActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử mua hàng", Toast.LENGTH_LONG).show();
        }
    }
}