package com.example.doan.adminactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.PremiumPackageAdapter;
import com.example.doan.model.PremiumPackage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PremiumPackageManagementActivity extends AppCompatActivity implements PremiumPackageAdapter.OnPremiumPackageListener {

    private RecyclerView recyclerView;
    private PremiumPackageAdapter adapter;
    private List<PremiumPackage> packageList;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;

    private DatabaseReference packageRef;
    // Store listener reference để có thể remove
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_package_management);

        initViews();
        initFirebase();
        setupRecyclerView();
        loadPackages();

        // Kiểm tra quyền admin trước khi cho phép thao tác
        checkAdminPermission();
    }

    private void checkAdminPermission() {
        // Bỏ kiểm tra admin, cho phép tất cả user đã đăng nhập
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // Cho phép thao tác cho tất cả user đã đăng nhập
            fabAdd.setOnClickListener(v -> showAddPackageDialog());
            createSampleDataButton();
            Log.d("PremiumPackage", "User đã đăng nhập, cho phép quản lý gói premium");
        } else {
            // Ẩn các chức năng nếu chưa đăng nhập
            fabAdd.setVisibility(View.GONE);
            Toast.makeText(PremiumPackageManagementActivity.this,
                    "Vui lòng đăng nhập để quản lý gói premium.",
                    Toast.LENGTH_LONG).show();
            Log.d("PremiumPackage", "User chưa đăng nhập");
        }
    }

    private void createSampleDataButton() {
        // Thêm một button để tạo dữ liệu mẫu nếu Firebase trống
        fabAdd.setOnLongClickListener(v -> {
            createSampleData();
            return true;
        });
    }

    private void createSampleData() {
        new AlertDialog.Builder(this)
                .setTitle("Tạo dữ liệu mẫu")
                .setMessage("Bạn có muốn tạo các gói premium mẫu không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    addSamplePackages();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void addSamplePackages() {
        progressBar.setVisibility(View.VISIBLE);

        // Tạo gói 1 tháng theo format Firebase của bạn
        PremiumPackage package1Month = new PremiumPackage("1 Tháng", 39000, 30);

        // Tạo gói 6 tháng theo format Firebase của bạn
        PremiumPackage package6Months = new PremiumPackage("6 Tháng", 199000, 180);

        // Tạo gói 1 năm theo format Firebase của bạn
        PremiumPackage package1Year = new PremiumPackage("1 Năm", 399000, 365);

        // Lưu các gói vào Firebase với key tự định nghĩa
        packageRef.child("goi1").setValue(package1Month);
        packageRef.child("goi2").setValue(package6Months);
        packageRef.child("goi3").setValue(package1Year)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã tạo 3 gói premium mẫu thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tạo dữ liệu mẫu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewPackages);
        fabAdd = findViewById(R.id.fabAddPackage);
        progressBar = findViewById(R.id.progressBar);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý gói Premium");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFirebase() {
        // Sử dụng Firebase URL và reference đến node "premium"
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        packageRef = database.getReference("premium");

        // Log để debug
        Log.d("PremiumPackage", "Firebase URL: https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        Log.d("PremiumPackage", "Firebase Reference: premium");
    }

    private void setupRecyclerView() {
        packageList = new ArrayList<>();
        adapter = new PremiumPackageAdapter(packageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadPackages() {
        progressBar.setVisibility(View.VISIBLE);

        Log.d("PremiumPackage", "Bắt đầu tải dữ liệu từ Firebase...");

        // Store listener reference để cleanup sau này
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("PremiumPackage", "Nhận dữ liệu từ Firebase: " + snapshot.getChildrenCount() + " items");

                // Process data efficiently
                List<PremiumPackage> tempList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        PremiumPackage package_ = dataSnapshot.getValue(PremiumPackage.class);
                        if (package_ != null) {
                            package_.setId(dataSnapshot.getKey());
                            tempList.add(package_);
                        }
                    } catch (Exception e) {
                        Log.e("PremiumPackage", "Error parsing package: " + e.getMessage());
                    }
                }

                // Update UI on main thread efficiently
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) { // Check activity state
                        packageList.clear();
                        packageList.addAll(tempList);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                        if (packageList.isEmpty()) {
                            Toast.makeText(PremiumPackageManagementActivity.this,
                                    "Không có dữ liệu gói premium. Vui lòng thêm gói mới.", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("PremiumPackage", "UI updated with " + packageList.size() + " packages");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PremiumPackage", "Lỗi Firebase: " + error.getMessage());
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PremiumPackageManagementActivity.this,
                                "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        packageRef.addValueEventListener(valueEventListener);
    }

    private void showAddPackageDialog() {
        showEditPackageDialog(null);
    }

    private void showEditPackageDialog(PremiumPackage package_) {
        boolean isEdit = package_ != null;

        // Inflate custom layout for dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_premium_package, null);

        // Find views in dialog layout
        EditText etName = dialogView.findViewById(R.id.etPackageName);
        EditText etDescription = dialogView.findViewById(R.id.etPackageDescription);
        EditText etPrice = dialogView.findViewById(R.id.etPackagePrice);
        EditText etDuration = dialogView.findViewById(R.id.etPackageDuration);
        EditText etFeatures = dialogView.findViewById(R.id.etPackageFeatures);

        // Xử lý null safety và prefill dữ liệu
        if (isEdit && package_ != null) {
            etName.setText(package_.getTenGoi() != null ? package_.getTenGoi() : "");
            etDescription.setText(package_.getDescription() != null ? package_.getDescription() : "");
            etPrice.setText(String.valueOf(package_.getGia()));
            etDuration.setText(String.valueOf(package_.getNgaySD()));
            etFeatures.setText(package_.getFeatures() != null ? package_.getFeatures() : "");
        } else {
            // Set default values cho gói mới
            etFeatures.setText("Đọc truyện premium,Không quảng cáo,Tải offline");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(isEdit ? "Sửa gói Premium" : "Thêm gói Premium")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Cập nhật" : "Thêm", null) // Set null để tránh auto dismiss
                .setNegativeButton("Hủy", null) // Set null để tránh auto dismiss
                .create();

        // Xử lý button click sau khi dialog được show để tránh gọi nhiều lần
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Đảm bảo chỉ set listener một lần
            positiveButton.setOnClickListener(v -> {
                // Disable button ngay lập tức để tránh multiple clicks
                positiveButton.setEnabled(false);

                String name = etName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String durationStr = etDuration.getText().toString().trim();
                String features = etFeatures.getText().toString().trim();

                if (validateInput(name, description, priceStr, durationStr)) {
                    try {
                        double price = Double.parseDouble(priceStr);
                        int duration = Integer.parseInt(durationStr);

                        // Hide keyboard trước khi thực hiện action
                        hideKeyboardAndClearFocus();

                        if (isEdit && package_ != null) {
                            updatePackage(package_.getId(), name, description, price, duration, features);
                        } else {
                            addPackage(name, description, price, duration, features);
                        }
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Giá và thời hạn phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                        // Re-enable button nếu có lỗi
                        positiveButton.setEnabled(true);
                    }
                } else {
                    // Re-enable button nếu validation fail
                    positiveButton.setEnabled(true);
                }
            });

            negativeButton.setOnClickListener(v -> {
                hideKeyboardAndClearFocus();
                dialog.dismiss();
            });
        });

        // Show dialog
        dialog.show();

        // Set window flags để tránh IME issues
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void hideKeyboardAndClearFocus() {
        // Hide keyboard với check null safety
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // Chỉ hide keyboard nếu đang visible
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Log.d("PremiumPackage", "Keyboard hidden successfully");
                }
            }
            currentFocus.clearFocus();
        }
    }

    private boolean validateInput(String name, String description, String price, String duration) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập tên gói", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "Vui lòng nhập thời hạn", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Double.parseDouble(price);
            Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá và thời hạn phải là số", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addPackage(String name, String description, double price, int duration, String features) {
        progressBar.setVisibility(View.VISIBLE);

        // Bỏ kiểm tra admin, thực hiện trực tiếp
        performAddPackage(name, description, price, duration, features);
    }

    private void performAddPackage(String name, String description, double price, int duration, String features) {
        // Tạo package mới
        PremiumPackage newPackage = new PremiumPackage(name, price, duration, description, features);
        String packageId = packageRef.push().getKey();

        // Log thông tin debug
        Log.d("PremiumPackage", "Adding new package with ID: " + packageId);
        Log.d("PremiumPackage", "User UID: " + (FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "null"));

        if (packageId != null) {
            packageRef.child(packageId).setValue(newPackage)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Thêm gói thành công", Toast.LENGTH_SHORT).show();
                        Log.d("PremiumPackage", "Add successful for package: " + packageId);
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Lỗi thêm gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("PremiumPackage", "Add failed: " + e.getMessage());
                        Log.e("PremiumPackage", "Error details: " + e.toString());
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Lỗi tạo ID cho gói mới", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePackage(String packageId, String name, String description, double price, int duration, String features) {
        progressBar.setVisibility(View.VISIBLE);

        // Bỏ kiểm tra admin, thực hiện trực tiếp
        performUpdatePackage(packageId, name, description, price, duration, features);
    }

    private void performUpdatePackage(String packageId, String name, String description, double price, int duration, String features) {
        // Tạo object update hoàn chỉnh
        PremiumPackage updatedPackage = new PremiumPackage(name, price, duration, description, features);

        DatabaseReference packageUpdateRef = packageRef.child(packageId);

        // Log thông tin debug
        Log.d("PremiumPackage", "Updating package: " + packageId);
        Log.d("PremiumPackage", "User UID: " + (FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "null"));

        // Sử dụng setValue để update toàn bộ object
        packageUpdateRef.setValue(updatedPackage)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Cập nhật gói thành công", Toast.LENGTH_SHORT).show();
                    Log.d("PremiumPackage", "Update successful for package: " + packageId);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi cập nhật gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PremiumPackage", "Update failed: " + e.getMessage());
                    Log.e("PremiumPackage", "Error details: " + e.toString());
                });
    }

    private void deletePackage(String packageId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa gói này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);

                    packageRef.child(packageId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Xóa gói thành công", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Lỗi xóa gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cleanup Firebase listeners để tránh memory leaks
        if (packageRef != null) {
            packageRef.removeEventListener(valueEventListener);
        }

        // Clear adapter và list
        if (adapter != null) {
            adapter = null;
        }
        if (packageList != null) {
            packageList.clear();
        }

        Log.d("PremiumPackage", "Activity destroyed, cleaned up resources");
    }

    // Implement interface methods
    @Override
    public void onEditClick(PremiumPackage package_) {
        showEditPackageDialog(package_);
    }

    @Override
    public void onDeleteClick(PremiumPackage package_) {
        deletePackage(package_.getId());
    }

    @Override
    public void onEditPackage(PremiumPackage premiumPackage) {
        showEditPackageDialog(premiumPackage);
    }

    @Override
    public void onDeletePackage(PremiumPackage premiumPackage) {
        deletePackage(premiumPackage.getId());
    }

    @Override
    public void onToggleActiveStatus(PremiumPackage premiumPackage, boolean isActive) {
        progressBar.setVisibility(View.VISIBLE);

        packageRef.child(premiumPackage.getId()).child("active").setValue(isActive)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    String status = isActive ? "kích hoạt" : "vô hiệu hóa";
                    Toast.makeText(this, "Đã " + status + " gói thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
