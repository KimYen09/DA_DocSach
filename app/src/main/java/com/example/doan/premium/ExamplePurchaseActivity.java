package com.example.doan.premium;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;

/**
 * Ví dụ về cách sử dụng PurchaseManager để lưu lịch sử mua hàng
 * Bạn có thể tích hợp code này vào Activity mua gói hiện có của bạn
 */
public class ExamplePurchaseActivity extends AppCompatActivity {

    private PurchaseManager purchaseManager;
    private Button btnBuyPackage1, btnBuyPackage2, btnViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.your_purchase_layout);

        // Khởi tạo PurchaseManager
        purchaseManager = new PurchaseManager(this);

        // Ví dụ: Khi người dùng nhấn mua gói
        setupPurchaseButtons();
    }

    private void setupPurchaseButtons() {
        // Ví dụ cho gói Premium 1 tháng
        // btnBuyPackage1 = findViewById(R.id.btnBuyPackage1);
        // btnBuyPackage1.setOnClickListener(v -> {
        //     processPurchase("Gói Premium 1 tháng", "99,000 VNĐ");
        // });

        // Ví dụ cho gói Premium 1 năm
        // btnBuyPackage2 = findViewById(R.id.btnBuyPackage2);
        // btnBuyPackage2.setOnClickListener(v -> {
        //     processPurchase("Gói Premium 1 năm", "999,000 VNĐ");
        // });

        // Nút xem lịch sử
        // btnViewHistory = findViewById(R.id.btnViewHistory);
        // btnViewHistory.setOnClickListener(v -> {
        //     openPurchaseHistory();
        // });
    }

    /**
     * Xử lý mua gói - gọi hàm này sau khi thanh toán thành công
     */
    private void processPurchase(String packageName, String packagePrice) {
        // Hiển thị loading nếu cần
        Toast.makeText(this, "Đang xử lý giao dịch...", Toast.LENGTH_SHORT).show();

        // Lưu thông tin mua hàng vào Firebase
        purchaseManager.savePurchaseRecord(packageName, packagePrice, new PurchaseManager.PurchaseCallback() {
            @Override
            public void onSuccess(PurchaseRecord purchaseRecord) {
                // Mua hàng thành công
                Toast.makeText(ExamplePurchaseActivity.this,
                    "Mua gói thành công! ID: " + purchaseRecord.getPurchaseId(),
                    Toast.LENGTH_LONG).show();

                // Có thể chuyển đến màn hình lịch sử mua hàng
                // openPurchaseHistory();

                // Hoặc cập nhật UI để hiển thị gói đã mua
                // updateUIAfterPurchase();
            }

            @Override
            public void onFailure(String error) {
                // Xử lý lỗi
                Toast.makeText(ExamplePurchaseActivity.this,
                    "Lỗi lưu lịch sử: " + error,
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Mở màn hình lịch sử mua hàng
     */
    private void openPurchaseHistory() {
        Intent intent = new Intent(this, LichSuMuaHangActivity.class);
        startActivity(intent);
    }

    // ===== CÁCH TÍCH HỢP VÀO CODE HIỆN CÓ =====

    /**
     * Ví dụ tích hợp với Google Play Billing hoặc payment gateway khác
     */
    private void onPaymentSuccess(String packageName, String packagePrice) {
        // Sau khi thanh toán thành công, lưu vào Firebase
        purchaseManager.savePurchaseRecord(packageName, packagePrice,
            new PurchaseManager.PurchaseCallback() {
                @Override
                public void onSuccess(PurchaseRecord purchaseRecord) {
                    // Thanh toán và lưu dữ liệu thành công
                    showSuccessDialog(purchaseRecord);
                }

                @Override
                public void onFailure(String error) {
                    // Thanh toán thành công nhưng lưu dữ liệu thất bại
                    // Vẫn cho người dùng sử dụng gói, nhưng ghi log lỗi
                    Toast.makeText(ExamplePurchaseActivity.this,
                        "Mua gói thành công nhưng không thể lưu lịch sử",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showSuccessDialog(PurchaseRecord record) {
        // Hiển thị dialog thành công với thông tin giao dịch
        Toast.makeText(this,
            "Mua thành công!\nGói: " + record.getPackageName() +
            "\nNgày: " + record.getPurchaseDate() +
            "\nID: " + record.getPurchaseId(),
            Toast.LENGTH_LONG).show();
    }
}
