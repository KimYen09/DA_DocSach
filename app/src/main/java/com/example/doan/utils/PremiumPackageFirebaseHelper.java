package com.example.doan.utils;

import androidx.annotation.NonNull;

import com.example.doan.model.PremiumPackage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PremiumPackageFirebaseHelper {
    private static final String PREMIUM_PACKAGES_NODE = "premium_packages";
    private DatabaseReference packageRef;

    public interface OnPackageLoadListener {
        void onSuccess(List<PremiumPackage> packages);
        void onFailure(String error);
    }

    public interface OnPackageOperationListener {
        void onSuccess();
        void onFailure(String error);
    }

    public PremiumPackageFirebaseHelper() {
        packageRef = FirebaseDatabase.getInstance().getReference(PREMIUM_PACKAGES_NODE);
    }

    // Lấy tất cả gói premium
    public void getAllPackages(OnPackageLoadListener listener) {
        packageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PremiumPackage> packages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PremiumPackage package_ = dataSnapshot.getValue(PremiumPackage.class);
                    if (package_ != null) {
                        package_.setId(dataSnapshot.getKey());
                        packages.add(package_);
                    }
                }
                listener.onSuccess(packages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    // Thêm gói premium mới
    public void addPackage(PremiumPackage package_, OnPackageOperationListener listener) {
        String packageId = packageRef.push().getKey();
        if (packageId != null) {
            packageRef.child(packageId).setValue(package_)
                    .addOnSuccessListener(aVoid -> listener.onSuccess())
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            listener.onFailure("Không thể tạo ID cho gói mới");
        }
    }

    // Cập nhật gói premium
    public void updatePackage(String packageId, PremiumPackage package_, OnPackageOperationListener listener) {
        packageRef.child(packageId).setValue(package_)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Xóa gói premium
    public void deletePackage(String packageId, OnPackageOperationListener listener) {
        packageRef.child(packageId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Lấy gói premium theo ID
    public void getPackageById(String packageId, OnPackageLoadListener listener) {
        packageRef.child(packageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PremiumPackage package_ = snapshot.getValue(PremiumPackage.class);
                List<PremiumPackage> packages = new ArrayList<>();
                if (package_ != null) {
                    package_.setId(snapshot.getKey());
                    packages.add(package_);
                }
                listener.onSuccess(packages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    // Lấy các gói đang hoạt động
    public void getActivePackages(OnPackageLoadListener listener) {
        packageRef.orderByChild("active").equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PremiumPackage> packages = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            PremiumPackage package_ = dataSnapshot.getValue(PremiumPackage.class);
                            if (package_ != null && package_.isActive()) {
                                package_.setId(dataSnapshot.getKey());
                                packages.add(package_);
                            }
                        }
                        listener.onSuccess(packages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }
}