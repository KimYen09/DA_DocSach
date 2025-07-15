package com.example.doan.premium;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.PurchaseHistoryAdapter; // You'll create this next
import com.example.doan.premium.PurchaseRecord;       // Your PurchaseRecord model
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LichSuMuaHangActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPurchaseHistory;
    private PurchaseHistoryAdapter adapter;
    private List<PurchaseRecord> purchaseList;
    private DatabaseReference userPurchaseHistoryRef;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView tvNoHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_mua_hang); // You'll create this layout next

        Toolbar toolbar = findViewById(R.id.toolbar_lich_su_mua_hang);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch Sử Mua Hàng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }

        recyclerViewPurchaseHistory = findViewById(R.id.recyclerViewPurchaseHistory);
        progressBar = findViewById(R.id.progressBarPurchaseHistory);
        tvNoHistory = findViewById(R.id.tvNoPurchaseHistory);

        recyclerViewPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));
        purchaseList = new ArrayList<>();
        adapter = new PurchaseHistoryAdapter(this, purchaseList);
        recyclerViewPurchaseHistory.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userPurchaseHistoryRef = FirebaseDatabase.getInstance().getReference("PurchaseHistory").child(userId);
            loadPurchaseHistory();
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử.", Toast.LENGTH_LONG).show();
            tvNoHistory.setText("Vui lòng đăng nhập.");
            tvNoHistory.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            // finish(); // Optionally close if user must be logged in
        }
    }

    private void loadPurchaseHistory() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoHistory.setVisibility(View.GONE);

        // Query to order by timestamp descending (newest first)
        Query query = userPurchaseHistoryRef.orderByChild("timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        PurchaseRecord record = recordSnapshot.getValue(PurchaseRecord.class);
                        if (record != null) {
                            purchaseList.add(record);
                        }
                    }
                    Collections.reverse(purchaseList); // To show newest first as Firebase returns ascending for orderByChild
                    adapter.notifyDataSetChanged();
                    tvNoHistory.setVisibility(View.GONE);
                } else {
                    tvNoHistory.setText("Chưa có lịch sử mua hàng.");
                    tvNoHistory.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LichSuMuaHangActivity.this, "Lỗi tải lịch sử mua hàng.", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Failed to load purchase history", error.toException());
                tvNoHistory.setText("Lỗi tải dữ liệu.");
                tvNoHistory.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle Toolbar back button press
        return true;
    }
}