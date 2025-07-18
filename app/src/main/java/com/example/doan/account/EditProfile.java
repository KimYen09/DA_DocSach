//package com.example.doan.account;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.doan.R;
//import com.example.doan.model.User;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.util.HashMap;
//
//public class EditProfile extends AppCompatActivity {
//
//    private ImageView imgAvatar;
//    private EditText edtUsername, edtEmail;
//    private Button btnChangeAvatar, btnSave;
//    private DatabaseReference userRef;
//    private FirebaseUser currentUser;
//    private Uri imageUri;
//    private StorageReference storageRef;
//    private ProgressDialog progressDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_profile);
//
//        imgAvatar = findViewById(R.id.imgAvatar);
//        edtUsername = findViewById(R.id.edtUsername);
//        edtEmail = findViewById(R.id.edtEmail);
//        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
//        btnSave = findViewById(R.id.btnSave);
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Đang cập nhật...");
//
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
//            storageRef = FirebaseStorage.getInstance().getReference("avatars").child(currentUser.getUid() + ".jpg");
//            loadUserData();
//        }
//
//        btnChangeAvatar.setOnClickListener(v -> chooseImage());
//        btnSave.setOnClickListener(v -> saveUserData());
//    }
//
//    private void loadUserData() {
//        userRef.get().addOnSuccessListener(snapshot -> {
//            if (snapshot.exists()) {
//                User user = snapshot.getValue(User.class);
//                if (user != null) {
//                    edtUsername.setText(user.getUsername());
//                    edtEmail.setText(user.getEmail());
//
//                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
//                        Glide.with(EditProfile.this).load(user.getAvatar()).into(imgAvatar);
//                    }
//                }
//            }
//        }).addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
//    }
//
//    private void chooseImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, 100);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
//            imageUri = data.getData();
//            imgAvatar.setImageURI(imageUri);
//        }
//    }
//
//    private void saveUserData() {
//        progressDialog.show();
//        String username = edtUsername.getText().toString().trim();
//
//        if (username.isEmpty()) {
//            edtUsername.setError("Tên không được để trống!");
//            progressDialog.dismiss();
//            return;
//        }
//
//        HashMap<String, Object> updates = new HashMap<>();
//        updates.put("username", username);
//
//        if (imageUri != null) {
//            storageRef.putFile(imageUri).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        updates.put("avatar", uri.toString());
//                        updateFirebase(updates);
//                    });
//                } else {
//                    progressDialog.dismiss();
//                    Toast.makeText(EditProfile.this, "Lỗi tải ảnh!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            updateFirebase(updates);
//        }
//    }
//
//    private void updateFirebase(HashMap<String, Object> updates) {
//        userRef.updateChildren(updates).addOnCompleteListener(task -> {
//            progressDialog.dismiss();
//            if (task.isSuccessful()) {
//                Toast.makeText(EditProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(EditProfile.this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void saveImageUrlToFirestore(String imageUrl) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document("userId");
//
//        userRef.update("profileImage", imageUrl)
//                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Image URL saved successfully"))
//                .addOnFailureListener(e -> Log.e("Firestore", "Failed to save URL: " + e.getMessage()));
//    }
//
//}
package com.example.doan.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private ImageView editImgAvatar;
    private Button btnChangeAvatar;
    private EditText editTxtUsername;
    private EditText editTxtEmail; // <-- KHAI BÁO BIẾN MỚI CHO EMAIL
    private Button btnSaveProfile, btnCancelEdit;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    private String currentAvatarDrawableName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editImgAvatar = findViewById(R.id.edit_imgAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        editTxtUsername = findViewById(R.id.edit_txtUsername);
        editTxtEmail = findViewById(R.id.edit_txtEmail); // <-- ÁNH XẠ BIẾN MỚI
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCancelable(false);

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để chỉnh sửa hồ sơ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        loadCurrentProfile();

        btnChangeAvatar.setOnClickListener(v -> showChangeAvatarDialog());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    private void loadCurrentProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class); // <-- LẤY EMAIL HIỆN TẠI
                    currentAvatarDrawableName = snapshot.child("avatarUrl").getValue(String.class);

                    if (editTxtUsername != null) {
                        editTxtUsername.setText(username);
                    } else {
                        Log.e(TAG, "editTxtUsername is null. Check activity_edit_profile.xml ID.");
                    }

                    if (editTxtEmail != null) { // <-- HIỂN THỊ EMAIL HIỆN TẠI
                        editTxtEmail.setText(email);
                    } else {
                        Log.e(TAG, "editTxtEmail is null. Check activity_edit_profile.xml ID.");
                    }

                    if (currentAvatarDrawableName != null && !currentAvatarDrawableName.isEmpty()) {
                        int resourceId = getResources().getIdentifier(
                                currentAvatarDrawableName, "drawable", getPackageName());

                        if (resourceId != 0) {
                            editImgAvatar.setImageResource(resourceId);
                        } else {
                            editImgAvatar.setImageResource(R.drawable.avatar);
                            Log.e(TAG, "Không tìm thấy drawable với tên: " + currentAvatarDrawableName);
                        }
                    } else {
                        editImgAvatar.setImageResource(R.drawable.avatar);
                    }
                } else {
                    Toast.makeText(EditProfile.this, "Không tìm thấy thông tin hồ sơ.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi tải thông tin hồ sơ hiện tại: " + error.getMessage());
                Toast.makeText(EditProfile.this, "Lỗi tải thông tin hồ sơ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangeAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi ảnh đại diện");
        builder.setMessage("Nhập tên file ảnh (không bao gồm .png, .jpg) từ thư mục drawable của bạn:");

        final EditText input = new EditText(this);
        input.setHint("Ví dụ: avatar_new");
        if (currentAvatarDrawableName != null && !currentAvatarDrawableName.isEmpty()) {
            input.setText(currentAvatarDrawableName);
        }
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newDrawableName = input.getText().toString().trim();
            if (newDrawableName.isEmpty()) {
                Toast.makeText(this, "Tên ảnh không được để trống.", Toast.LENGTH_SHORT).show();
            } else {
                currentAvatarDrawableName = newDrawableName;
                int resourceId = getResources().getIdentifier(newDrawableName, "drawable", getPackageName());
                if (resourceId != 0) {
                    editImgAvatar.setImageResource(resourceId);
                } else {
                    editImgAvatar.setImageResource(R.drawable.avatar);
                    Toast.makeText(this, "Không tìm thấy ảnh drawable này.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveProfile() {
        String newUsername = editTxtUsername.getText().toString().trim();
        String newEmail = editTxtEmail.getText().toString().trim(); // <-- LẤY EMAIL MỚI

        if (newUsername.isEmpty()) {
            editTxtUsername.setError("Tên người dùng không được để trống!");
            editTxtUsername.requestFocus();
            return;
        }
        if (newEmail.isEmpty()) {
            editTxtEmail.setError("Email không được để trống!");
            editTxtEmail.requestFocus();
            return;
        }
        // Kiểm tra định dạng email cơ bản (tùy chọn)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editTxtEmail.setError("Email không hợp lệ!");
            editTxtEmail.requestFocus();
            return;
        }

        progressDialog.show();

        // Gọi phương thức cập nhật profile với tên người dùng, email và tên drawable
        updateProfileInDatabase(newUsername, newEmail, currentAvatarDrawableName);
    }

    private void updateProfileInDatabase(String newUsername, String newEmail, String newAvatarName) { // <-- THÊM THAM SỐ newEmail
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);
        updates.put("email", newEmail); // <-- THÊM EMAIL VÀO UPDATES
        if (newAvatarName != null && !newAvatarName.isEmpty()) {
            updates.put("avatarUrl", newAvatarName);
        } else {
            updates.put("avatarUrl", "avatar");
        }

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this, "Lỗi cập nhật hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi cập nhật hồ sơ vào Database: " + e.getMessage());
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