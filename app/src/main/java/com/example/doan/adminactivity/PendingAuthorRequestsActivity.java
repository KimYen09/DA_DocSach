//package com.example.doan.adminactivity; // Đặt trong package admin của bạn
//
//import android.app.ProgressDialog;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.doan.R;
//import com.example.doan.adapter.PendingAuthorRequestAdapter; // Import adapter mới
//import com.example.doan.model.User; // Import User model
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class PendingAuthorRequestsActivity extends AppCompatActivity {
//
//    private static final String TAG = "PendingAuthorRequests";
//
//    private RecyclerView recyclerViewPendingRequests;
//    private PendingAuthorRequestAdapter adapter;
//    private List<User> pendingRequestList;
//    private ProgressBar progressBarRequests;
//    private TextView tvPendingRequestsCount;
//    private ImageButton btnBack;
//
//    private DatabaseReference usersRef; // Tham chiếu đến node "users"
//
//    private ProgressDialog progressDialog;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pending_author_requests);
//
//
//        // Ánh xạ View
//        recyclerViewPendingRequests = findViewById(R.id.recyclerViewPendingRequests);
//        progressBarRequests = findViewById(R.id.progressBarRequests);
//        tvPendingRequestsCount = findViewById(R.id.tvPendingRequestsCount);
//        btnBack = findViewById(R.id.btnBack);
//
//        // Khởi tạo Firebase Database Reference
//        usersRef = FirebaseDatabase.getInstance().getReference("users");
//
//        // Log để kiểm tra xem tham chiếu có null không ngay sau khi khởi tạo
//        Log.d(TAG, "usersRef initialized: " + (usersRef != null));
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Đang tải yêu cầu...");
//        progressDialog.setCancelable(false);
//
//        recyclerViewPendingRequests.setLayoutManager(new LinearLayoutManager(this));
//        pendingRequestList = new ArrayList<>();
//
//        adapter = new PendingAuthorRequestAdapter(this, pendingRequestList, new PendingAuthorRequestAdapter.OnRequestListener() {
//            @Override
//            public void onApprove(User user) {
//                approveAuthorRequest(user);
//            }
//
//            @Override
//            public void onReject(User user) {
//                rejectAuthorRequest(user);
//            }
//        });
//        recyclerViewPendingRequests.setAdapter(adapter);
//
//        loadPendingAuthorRequests();
//
//        btnBack.setOnClickListener(v -> finish());
//    }
//
//    private void loadPendingAuthorRequests() {
//        progressBarRequests.setVisibility(View.VISIBLE);
//        progressDialog.show();
//
//        if (usersRef == null) {
//            Log.e(TAG, "usersRef is null before calling addListenerForSingleValueEvent. Firebase might not be initialized.");
//            Toast.makeText(this, "Lỗi: Không thể tải yêu cầu (tham chiếu Firebase null).", Toast.LENGTH_LONG).show();
//            progressBarRequests.setVisibility(View.GONE);
//            progressDialog.dismiss();
//            return;
//        }
//
//        // Truy vấn node 'users' và lọc theo 'requestStatus'
//        usersRef.orderByChild("requestStatus").equalTo("pending_author")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        pendingRequestList.clear();
//                        if (snapshot.exists()) {
//                            Log.d(TAG, "Found " + snapshot.getChildrenCount() + " potential pending author requests.");
//                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                                User user = userSnapshot.getValue(User.class);
//                                if (user != null) {
//                                    user.setUserId(userSnapshot.getKey()); // Đảm bảo userId được set từ key
//                                    // Log chi tiết để kiểm tra giá trị
//                                    Log.d(TAG, "Processing user ID: " + user.getUserId() +
//                                            ", Username: " + user.getUsername() +
//                                            ", Role: " + user.getRole() +
//                                            ", RequestStatus: " + user.getRequestStatus() +
//                                            ", AuthorName: " + user.getAuthorName() +
//                                            ", AuthorBio: " + user.getAuthorBio());
//
//                                    // Thêm vào danh sách (điều kiện lọc đã nằm trong truy vấn Firebase)
//                                    pendingRequestList.add(user);
//                                    Log.d(TAG, "Added pending author: " + user.getUsername());
//                                } else {
//                                    Log.w(TAG, "User object is null for ID: " + userSnapshot.getKey() + ". Skipping.");
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "No pending_author requests found in Firebase for current query.");
//                        }
//                        adapter.notifyDataSetChanged();
//                        tvPendingRequestsCount.setText("Tổng số yêu cầu: " + pendingRequestList.size());
//                        progressBarRequests.setVisibility(View.GONE);
//                        progressDialog.dismiss();
//
//                        if (pendingRequestList.isEmpty()) {
//                            Toast.makeText(PendingAuthorRequestsActivity.this, "Không có yêu cầu chờ duyệt.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e(TAG, "Failed to load pending author requests from /users node: " + error.getMessage());
//                        Toast.makeText(PendingAuthorRequestsActivity.this, "Lỗi tải yêu cầu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                        progressBarRequests.setVisibility(View.GONE);
//                        progressDialog.dismiss();
//                    }
//                });
//    }
//
//    private void approveAuthorRequest(User user) {
//        if (user == null || user.getUserId() == null) return;
//
//        progressDialog.setMessage("Đang duyệt yêu cầu...");
//        progressDialog.show();
//
//        // Cập nhật node 'users'
//        Map<String, Object> userUpdates = new HashMap<>();
//        userUpdates.put("role", "author"); // Đổi vai trò thành "author"
//        userUpdates.put("requestStatus", "approved_author"); // Đặt trạng thái đã duyệt
//        // authorName và authorBio đã được lưu trong node users, không cần thêm/xóa riêng
//
//        usersRef.child(user.getUserId()).updateChildren(userUpdates)
//                .addOnSuccessListener(aVoid -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(PendingAuthorRequestsActivity.this, "Đã duyệt tác giả: " + user.getUsername(), Toast.LENGTH_LONG).show();
//                    loadPendingAuthorRequests(); // Tải lại danh sách để cập nhật UI
//                })
//                .addOnFailureListener(e -> {
//                    progressDialog.dismiss();
//                    Log.e(TAG, "Failed to approve author request for " + user.getUserId() + ": " + e.getMessage());
//                    Toast.makeText(PendingAuthorRequestsActivity.this, "Lỗi duyệt yêu cầu: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//    }
//
//    private void rejectAuthorRequest(User user) {
//        if (user == null || user.getUserId() == null) return;
//
//        progressDialog.setMessage("Đang từ chối yêu cầu...");
//        progressDialog.show();
//
//        // Cập nhật node 'users'
//        Map<String, Object> userUpdates = new HashMap<>();
//        userUpdates.put("requestStatus", "rejected_author");
//        // Không thay đổi role, giữ nguyên là "user"
//        // authorName và authorBio đã được lưu trong node users, không cần thêm/xóa riêng
//
//        usersRef.child(user.getUserId()).updateChildren(userUpdates)
//                .addOnSuccessListener(aVoid -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(PendingAuthorRequestsActivity.this, "Đã từ chối tác giả: " + user.getUsername(), Toast.LENGTH_LONG).show();
//                    loadPendingAuthorRequests(); // Tải lại danh sách để cập nhật UI
//                })
//                .addOnFailureListener(e -> {
//                    progressDialog.dismiss();
//                    Log.e(TAG, "Failed to reject author request for " + user.getUserId() + ": " + e.getMessage());
//                    Toast.makeText(PendingAuthorRequestsActivity.this, "Lỗi cập nhật trạng thái người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
//}

// PendingAuthorRequestsActivity.java
package com.example.doan.adminactivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.PendingAuthorRequestAdapter;
import com.example.doan.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingAuthorRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPendingRequests;
    private TextView tvPendingRequestsCount;
    private ProgressBar progressBarRequests;
    private ImageButton btnBack;

    private DatabaseReference databaseReference;
    private PendingAuthorRequestAdapter adapter;
    private ArrayList<User> pendingRequestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_author_requests);

        // Ánh xạ các view từ file XML
        recyclerViewPendingRequests = findViewById(R.id.recyclerViewPendingRequests);
        tvPendingRequestsCount = findViewById(R.id.tvPendingRequestsCount);
        progressBarRequests = findViewById(R.id.progressBarRequests);
        btnBack = findViewById(R.id.btnBack);

        // Thiết lập RecyclerView
        recyclerViewPendingRequests.setLayoutManager(new LinearLayoutManager(this));
        pendingRequestsList = new ArrayList<>();
        adapter = new PendingAuthorRequestAdapter(this, pendingRequestsList);
        recyclerViewPendingRequests.setAdapter(adapter);

        // Thiết lập Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Lấy các yêu cầu tác giả đang chờ duyệt
        loadPendingAuthorRequests();

        // Xử lý sự kiện nhấn nút Quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // Trong PendingAuthorRequestsActivity.java
    private void loadPendingAuthorRequests() {
        progressBarRequests.setVisibility(View.VISIBLE);

        Query query = databaseReference.orderByChild("requestStatus").equalTo("pending_author");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pendingRequestsList.clear();

                Log.d("FirebaseDebug", "onDataChange triggered.");
                Log.d("FirebaseDebug", "DataSnapshot exists: " + dataSnapshot.exists());
                Log.d("FirebaseDebug", "Number of children found: " + dataSnapshot.getChildrenCount());

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setUserId(snapshot.getKey());
                            pendingRequestsList.add(user);
                            Log.d("FirebaseDebug", "Found user: " + user.getUsername() + " with status: " + user.getRequestStatus());
                        } else {
                            Log.e("FirebaseDebug", "Failed to parse user data for key: " + snapshot.getKey());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                tvPendingRequestsCount.setText("Tổng số yêu cầu: " + pendingRequestsList.size());
                progressBarRequests.setVisibility(View.GONE);
                Log.d("FirebaseDebug", "Adapter notified. Final list size: " + pendingRequestsList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBarRequests.setVisibility(View.GONE);
                Log.e("FirebaseDebug", "Database error: " + databaseError.getMessage());
            }
        });
    }
}