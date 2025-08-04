package com.example.doan.adminactivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.PremiumPackageAdapter;
import com.example.doan.model.PremiumPackage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PremiumManagementActivity extends AppCompatActivity implements PremiumPackageAdapter.OnPremiumPackageListener {
    private RecyclerView recyclerViewPremium;
    private FloatingActionButton fabAddPremium;
    private LinearLayout layoutEmpty;
    private ImageView btnBack;
    private PremiumPackageAdapter adapter;
    private List<PremiumPackage> packageList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_management);

        initViews();
        initFirebase();
        setupClickListeners();
        loadPremiumPackages();
    }

    private void initViews() {
        recyclerViewPremium = findViewById(R.id.recyclerViewPremium);
        fabAddPremium = findViewById(R.id.fab_add_premium);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnBack = findViewById(R.id.btnBack);

        packageList = new ArrayList<>();
        adapter = new PremiumPackageAdapter(packageList, this);
        recyclerViewPremium.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPremium.setAdapter(adapter);
    }

    private void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference("premium_packages");
    }

    private void setupClickListeners() {
        fabAddPremium.setOnClickListener(v -> showAddEditDialog(null));
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPremiumPackages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                packageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PremiumPackage premiumPackage = snapshot.getValue(PremiumPackage.class);
                    if (premiumPackage != null) {
                        premiumPackage.setId(snapshot.getKey());
                        packageList.add(premiumPackage);
                    }
                }
                adapter.updateData(packageList);
                updateEmptyState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PremiumManagementActivity.this,
                        "Lỗi khi tải dữ liệu: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (packageList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerViewPremium.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerViewPremium.setVisibility(View.VISIBLE);
        }
    }

    private void showAddEditDialog(PremiumPackage packageToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_premium, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etName = dialogView.findViewById(R.id.etPackageName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etPrice);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etDuration);
        TextInputEditText etFeatures = dialogView.findViewById(R.id.etFeatures);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Thiết lập tiêu đề và dữ liệu
        if (packageToEdit != null) {
            tvTitle.setText("Sửa gói Premium");
            etName.setText(packageToEdit.getName());
            etDescription.setText(packageToEdit.getDescription());
            etPrice.setText(String.valueOf(packageToEdit.getPrice()));
            etDuration.setText(String.valueOf(packageToEdit.getDuration()));
            etFeatures.setText(packageToEdit.getFeatures());
            btnSave.setText("Cập nhật");
        } else {
            tvTitle.setText("Thêm gói Premium");
            etFeatures.setText("Đọc truyện premium,Không quảng cáo,Tải offline");
            btnSave.setText("Thêm");
        }

        AlertDialog dialog = builder.setView(dialogView).create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
            String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
            String durationStr = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";
            String features = etFeatures.getText() != null ? etFeatures.getText().toString().trim() : "";

            // Kiểm tra validation
            if (name.isEmpty()) {
                etName.setError("Vui lòng nhập tên gói");
                etName.requestFocus();
                return;
            }
            if (description.isEmpty()) {
                etDescription.setError("Vui lòng nhập mô tả");
                etDescription.requestFocus();
                return;
            }
            if (priceStr.isEmpty()) {
                etPrice.setError("Vui lòng nhập giá");
                etPrice.requestFocus();
                return;
            }
            if (durationStr.isEmpty()) {
                etDuration.setError("Vui lòng nhập thời hạn");
                etDuration.requestFocus();
                return;
            }
            if (features.isEmpty()) {
                etFeatures.setError("Vui lòng nhập tính năng");
                etFeatures.requestFocus();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                int duration = Integer.parseInt(durationStr);

                if (price <= 0) {
                    etPrice.setError("Giá phải lớn hơn 0");
                    etPrice.requestFocus();
                    return;
                }
                if (duration <= 0) {
                    etDuration.setError("Thời hạn phải lớn hơn 0");
                    etDuration.requestFocus();
                    return;
                }

                if (packageToEdit != null) {
                    // Cập nhật gói
                    updatePremiumPackage(packageToEdit.getId(), name, description, price, duration, features);
                } else {
                    // Thêm gói mới
                    addPremiumPackage(name, description, price, duration, features);
                }
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ cho giá và thời hạn", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void addPremiumPackage(String name, String description, double price, int duration, String features) {
        // Sửa constructor để khớp với PremiumPackage mới: tenGoi, gia, ngaySD, description, features
        PremiumPackage newPackage = new PremiumPackage(name, price, duration, description, features);

        DatabaseReference newRef = databaseReference.push();
        newRef.setValue(newPackage)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm gói Premium thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi thêm gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePremiumPackage(String packageId, String name, String description, double price, int duration, String features) {
        // Sửa constructor để khớp với PremiumPackage mới: tenGoi, gia, ngaySD, description, features
        PremiumPackage updatedPackage = new PremiumPackage(name, price, duration, description, features);
        updatedPackage.setId(packageId);

        databaseReference.child(packageId).setValue(updatedPackage)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật gói Premium thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi cập nhật gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deletePremiumPackage(String packageId) {
        databaseReference.child(packageId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa gói Premium thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xóa gói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePackageActiveStatus(String packageId, boolean isActive) {
        databaseReference.child(packageId).child("active").setValue(isActive)
                .addOnSuccessListener(aVoid -> {
                    String status = isActive ? "kích hoạt" : "vô hiệu hóa";
                    Toast.makeText(this, "Đã " + status + " gói thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi cập nhật trạng thái: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Implement interface methods
    @Override
    public void onEditClick(PremiumPackage package_) {
        showAddEditDialog(package_);
    }

    @Override
    public void onDeleteClick(PremiumPackage package_) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa gói \"" + package_.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deletePremiumPackage(package_.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEditPackage(PremiumPackage premiumPackage) {
        showAddEditDialog(premiumPackage);
    }

    @Override
    public void onDeletePackage(PremiumPackage premiumPackage) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa gói \"" + premiumPackage.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deletePremiumPackage(premiumPackage.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onToggleActiveStatus(PremiumPackage premiumPackage, boolean isActive) {
        updatePackageActiveStatus(premiumPackage.getId(), isActive);
    }
}


