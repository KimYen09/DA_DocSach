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
                //Sau khi nhan xac nhan thanh toan hien thong bao thanh cong
                Toast.makeText(Payment.this,
                        "Thanh toán cho " + goiDuocChon + " thành công!", Toast.LENGTH_LONG).show();

                // Log the purchase date (optional, or handled by payment SDK)
                String purchaseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Log.d("PaymentConfirmation", "Confirmed purchase of " + phiCuaGoi + " on " + purchaseDate);

                // Sau khi thanh toan tro lai trang Home
                saveUserAsPremium(true);
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    savePurchaseToFirebase(userId, goiDuocChon, phiCuaGoi, purchaseDate);
                } else {
                    Log.e("Payment", "User not logged in, cannot save purchase history.");
                    // truong hop user chua dang nhap
                }

                Intent HomeIntent = new Intent(Payment.this, HomeFragment.class);
                HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                HomeIntent.putExtra("NAVIGATE_TO_HOME_AFTER_PAYMENT", true);
                startActivity(HomeIntent);
                finishAffinity();
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
    private void savePurchaseToFirebase(String userId, String packageName, String packagePrice, String purchaseDate) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // Structure: PurchaseHistory -> UserID -> PurchaseID -> PurchaseDetails
        String purchaseId = database.child("PurchaseHistory").child(userId).push().getKey(); // Generates a unique ID

        if (purchaseId == null) {
            Log.e("Firebase", "Couldn't get unique key for purchase history");
            Toast.makeText(this, "Lỗi lưu lịch sử mua hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        PurchaseRecord record = new PurchaseRecord(purchaseId, packageName, packagePrice, purchaseDate, System.currentTimeMillis());
        // Using System.currentTimeMillis() for sorting if needed

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
