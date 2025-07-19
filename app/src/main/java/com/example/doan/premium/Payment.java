package com.example.doan.premium;
import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.doan.R;
import com.example.doan.fragmenthome.HomeFragment;
// Import for date/time if you still want to log/use it here
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Payment extends AppCompatActivity {
    private TextView tvTenGoi, tvPhigoi, tvThongtingoi;
    private Button btnXacNhan, btnHuy;

    private String goiDuocChon;
    private String phiCuaGoi;

    private DatabaseReference LichSuMuaHang;
    private FirebaseAuth mAuth;
    private PurchaseManager purchaseManager; // Thêm PurchaseManager

    public static final String PREMIUM_PREFS_NAME = "PremiumPrefs";
    public static final String KEY_IS_USER_PREMIUM = "isUserPremium";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvTenGoi = findViewById(R.id.tvTenGoi);
        tvPhigoi = findViewById(R.id.tvPhigoi);
        tvThongtingoi = findViewById(R.id.tvThongtingoi);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnHuy = findViewById(R.id.btnHuy);
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo PurchaseManager
        purchaseManager = new PurchaseManager(this);

        // lay du lieu tu Premium.java
        Intent intent = getIntent();
        goiDuocChon = intent.getStringExtra("ten_goi");
        phiCuaGoi = intent.getStringExtra("phi_goi");

        if (goiDuocChon != null) {
            tvTenGoi.setText("Gói đã chọn: " + goiDuocChon);
        }
        if (phiCuaGoi != null) {
            tvPhigoi.setText("Giá: " + phiCuaGoi);
        }

        // hien ngay thanh toan hien tai
        tvThongtingoi.setText("Ngày thanh toán: " + getCurrentDate());

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPurchase();
            }
        });

        // Nút Cancel Thanh toán được nhấn
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Payment.this, "Thanh toán đã hủy.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void processPurchase() {
        // Hiển thị thông báo đang xử lý
        Toast.makeText(this, "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();

        // Kiểm tra người dùng đã đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("Payment", "User not logged in");
            Toast.makeText(this, "Vui lòng đăng nhập để mua gói", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Payment", "Processing purchase for user: " + currentUser.getUid());
        Log.d("Payment", "Package: " + goiDuocChon + " - Price: " + phiCuaGoi);

        // Lưu trạng thái Premium
        saveUserAsPremium(true);

        // Lưu lịch sử mua hàng vào Firebase
        purchaseManager.savePurchaseRecord(goiDuocChon, phiCuaGoi,
            new PurchaseManager.PurchaseCallback() {
                @Override
                public void onSuccess(PurchaseRecord purchaseRecord) {
                    // Thanh toán thành công
                    Log.d("Payment", "Purchase saved successfully: " + purchaseRecord.getPurchaseId());
                    Toast.makeText(Payment.this,
                        "Thanh toán cho " + goiDuocChon + " thành công!\nID: " + purchaseRecord.getPurchaseId(),
                        Toast.LENGTH_LONG).show();

                    // Chuyển về trang Home sau 2 giây để user đọc được thông báo
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigateToHome();
                        }
                    }, 2000);
                }

                @Override
                public void onFailure(String error) {
                    // Thanh toán thành công nhưng không lưu được lịch sử
                    Log.e("Payment", "Failed to save purchase history: " + error);
                    Toast.makeText(Payment.this,
                        "Thanh toán thành công nhưng không thể lưu lịch sử.\nLỗi: " + error,
                        Toast.LENGTH_LONG).show();

                    // Vẫn chuyển về trang Home vì thanh toán đã thành công
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigateToHome();
                        }
                    }, 3000);
                }
            });
    }

    private void navigateToHome() {
        Intent HomeIntent = new Intent(Payment.this, HomeFragment.class);
        HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        HomeIntent.putExtra("NAVIGATE_TO_HOME_AFTER_PAYMENT", true);
        startActivity(HomeIntent);
        finishAffinity();
    }

    // Giữ lại method cũ để backup (có thể xóa sau khi test)
    private void savePurchaseToFirebase(String userId, String packageName, String packagePrice, String purchaseDate) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String purchaseId = database.child("PurchaseHistory").child(userId).push().getKey();

        if (purchaseId == null) {
            Log.e("Firebase", "Couldn't get unique key for purchase history");
            Toast.makeText(this, "Lỗi lưu lịch sử mua hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        PurchaseRecord record = new PurchaseRecord(purchaseId, packageName, packagePrice, purchaseDate, System.currentTimeMillis());

        database.child("PurchaseHistory").child(userId).child(purchaseId).setValue(record)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Purchase history saved successfully for user: " + userId))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to save purchase history", e);
                    Toast.makeText(Payment.this, "Lỗi lưu lịch sử mua hàng.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserAsPremium(boolean isPremium) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREMIUM_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_USER_PREMIUM, isPremium);
        editor.apply();
        Log.d("Payment", "User premium status saved: " + isPremium);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
