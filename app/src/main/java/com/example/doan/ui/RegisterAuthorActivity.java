package com.example.doan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterAuthorActivity extends AppCompatActivity {

    private static final String TAG = "RegisterAuthorActivity";

    private EditText edtAuthorName;
    private EditText edtAuthorBio;
    private MaterialButton btnRegisterAuthor;
    private ImageButton btnBack;
    private TextView tvStatusMessage;
    private LinearLayout registrationFormLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_author);

        edtAuthorName = findViewById(R.id.edtAuthorName);
        edtAuthorBio = findViewById(R.id.edtAuthorBio);
        btnRegisterAuthor = findViewById(R.id.btnRegisterAuthor);
        btnBack = findViewById(R.id.btnBack);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        registrationFormLayout = findViewById(R.id.registrationFormLayout);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        btnBack.setOnClickListener(v -> finish());
        btnRegisterAuthor.setOnClickListener(v -> registerAuthor());

        checkUserStatus();
    }

    private void checkUserStatus() {
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đăng ký tác giả.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        progressDialog.show();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String currentRole = user.getRole();
                        String requestStatus = user.getRequestStatus();

                        // Nếu đã là admin/author hoặc yêu cầu đang chờ, ẩn form và hiển thị thông báo
                        if ("admin".equalsIgnoreCase(currentRole) || "author".equalsIgnoreCase(currentRole)) {
                            tvStatusMessage.setText("Bạn đã là " + currentRole + ". Không cần đăng ký lại.");
                            tvStatusMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            tvStatusMessage.setVisibility(View.VISIBLE);
                            registrationFormLayout.setVisibility(View.GONE);
                        } else if ("pending_author".equalsIgnoreCase(requestStatus)) {
                            tvStatusMessage.setText("Yêu cầu đăng ký tác giả của bạn đang chờ duyệt.");
                            tvStatusMessage.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                            tvStatusMessage.setVisibility(View.VISIBLE);
                            registrationFormLayout.setVisibility(View.GONE);
                        }
                        else {
                            // Nếu là "user" và không có yêu cầu nào đang chờ, hiển thị form
                            tvStatusMessage.setVisibility(View.GONE);
                            registrationFormLayout.setVisibility(View.VISIBLE);
                            edtAuthorName.setText(user.getUsername()); // Pre-fill with current username
                        }
                    } else {
                        Log.w(TAG, "User object is null for UID: " + currentUser.getUid());
                        tvStatusMessage.setText("Lỗi tải thông tin người dùng.");
                        tvStatusMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        tvStatusMessage.setVisibility(View.VISIBLE);
                        registrationFormLayout.setVisibility(View.GONE);
                    }
                } else {
                    Log.w(TAG, "User data not found for UID: " + currentUser.getUid());
                    tvStatusMessage.setText("Hồ sơ người dùng chưa tồn tại. Vui lòng thử lại sau.");
                    tvStatusMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tvStatusMessage.setVisibility(View.VISIBLE);
                    registrationFormLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.e(TAG, "Failed to load user role: " + error.getMessage());
                Toast.makeText(RegisterAuthorActivity.this, "Lỗi tải thông tin người dùng.", Toast.LENGTH_SHORT).show();
                tvStatusMessage.setText("Lỗi tải thông tin: " + error.getMessage());
                tvStatusMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvStatusMessage.setVisibility(View.VISIBLE);
                registrationFormLayout.setVisibility(View.GONE);
            }
        });
    }

    private void registerAuthor() {
        String authorName = edtAuthorName.getText().toString().trim();
        String authorBio = edtAuthorBio.getText().toString().trim();

        if (TextUtils.isEmpty(authorName)) {
            Toast.makeText(this, "Vui lòng nhập tên tác giả.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        // Không đổi role ngay lập tức. Chỉ thêm requestStatus
        updates.put("requestStatus", "pending_author"); // Trạng thái yêu cầu đang chờ duyệt
        updates.put("authorName", authorName); // Lưu tên tác giả
        updates.put("authorBio", authorBio); // Lưu bio tác giả

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterAuthorActivity.this, "Yêu cầu đăng ký tác giả đã được gửi. Vui lòng chờ duyệt.", Toast.LENGTH_LONG).show();
                    // Cập nhật UI sau khi gửi yêu cầu thành công
                    tvStatusMessage.setText("Yêu cầu đăng ký tác giả của bạn đang chờ duyệt.");
                    tvStatusMessage.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    tvStatusMessage.setVisibility(View.VISIBLE);
                    registrationFormLayout.setVisibility(View.GONE);
                    setResult(Activity.RESULT_OK); // Gửi kết quả OK về Activity gọi
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Failed to send author registration request: " + e.getMessage());
                    Toast.makeText(RegisterAuthorActivity.this, "Lỗi gửi yêu cầu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
