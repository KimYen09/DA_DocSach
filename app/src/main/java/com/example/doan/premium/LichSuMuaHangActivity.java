package com.example.doan.premium;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.PurchaseHistoryAdapter;
import com.example.doan.premium.PurchaseRecord;
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
    private LinearLayout layoutEmptyState;
    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_record);

        initViews();
        setupBackButton();
        setupRecyclerView();
        checkUserAndLoadHistory();
    }

    private void initViews() {
        recyclerViewPurchaseHistory = findViewById(R.id.recyclerViewPurchaseHistory);
        progressBar = findViewById(R.id.progressBarPurchaseHistory);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        ivBack = findViewById(R.id.ivBack);
    }

    private void setupBackButton() {
        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerViewPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));
        purchaseList = new ArrayList<>();
        adapter = new PurchaseHistoryAdapter(this, purchaseList);
        recyclerViewPurchaseHistory.setAdapter(adapter);
    }

    private void checkUserAndLoadHistory() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Sử dụng node giaoDich thay vì PurchaseHistory
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
            userPurchaseHistoryRef = database.getReference("giaoDich").child(userId);
            loadPurchaseHistory();
        } else {
            showNoUserMessage();
        }
    }

    private void showNoUserMessage() {
        Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử.", Toast.LENGTH_LONG).show();
        TextView tvNoHistory = findViewById(R.id.tvNoPurchaseHistory);
        tvNoHistory.setText("Vui lòng đăng nhập để xem lịch sử mua hàng.");
        tvNoHistory.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void loadPurchaseHistory() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewPurchaseHistory.setVisibility(View.GONE);

        Log.d("LichSuMuaHang", "Loading purchase history for user: " + mAuth.getCurrentUser().getUid());
        Log.d("LichSuMuaHang", "Firebase path: giaoDich/" + mAuth.getCurrentUser().getUid());

        // Query để sắp xếp theo timestamp giảm dần (mới nhất trước)
        Query query = userPurchaseHistoryRef.orderByChild("timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseList.clear();

                Log.d("LichSuMuaHang", "DataSnapshot exists: " + snapshot.exists());
                Log.d("LichSuMuaHang", "Children count: " + snapshot.getChildrenCount());

                if (snapshot.exists()) {
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        Log.d("LichSuMuaHang", "Processing record: " + recordSnapshot.getKey());
                        PurchaseRecord record = recordSnapshot.getValue(PurchaseRecord.class);
                        if (record != null) {
                            Log.d("LichSuMuaHang", "Record loaded: " + record.getPackageName() + " - " + record.getPurchaseDate());
                            purchaseList.add(record);
                        } else {
                            Log.w("LichSuMuaHang", "Record is null for key: " + recordSnapshot.getKey());
                        }
                    }

                    // Đảo ngược để hiển thị mới nhất trước
                    Collections.reverse(purchaseList);

                    if (purchaseList.size() > 0) {
                        Log.d("LichSuMuaHang", "Displaying " + purchaseList.size() + " records");
                        adapter.notifyDataSetChanged();
                        recyclerViewPurchaseHistory.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);
                    } else {
                        Log.d("LichSuMuaHang", "No valid records found");
                        showEmptyMessage();
                    }
                } else {
                    Log.d("LichSuMuaHang", "No data found in Firebase");
                    showEmptyMessage();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerViewPurchaseHistory.setVisibility(View.GONE);

                Log.e("LichSuMuaHang", "Firebase error: " + error.getMessage());
                Log.e("LichSuMuaHang", "Error code: " + error.getCode());
                Log.e("LichSuMuaHang", "Error details: " + error.getDetails());

                Toast.makeText(LichSuMuaHangActivity.this,
                    "Lỗi tải lịch sử mua hàng: " + error.getMessage(),
                    Toast.LENGTH_LONG).show();

                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvError = findViewById(R.id.tvNoPurchaseHistory);
                tvError.setText("Lỗi tải dữ liệu: " + error.getMessage() + "\nVui lòng thử lại.");
            }
        });
    }

    private void showEmptyMessage() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerViewPurchaseHistory.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data khi quay lại activity
        if (userPurchaseHistoryRef != null) {
            loadPurchaseHistory();
        }
    }
}