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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private ImageView imgAvatar;
    private EditText edtUsername, edtEmail;
    private Button btnChangeAvatar, btnSave;
    private FirebaseUser currentUser;
    private Uri imageUri;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnSave = findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật...");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Firebase Realtime Database Reference để lưu thông tin người dùng
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

            loadUserData(); // Tải dữ liệu người dùng
        }

        btnChangeAvatar.setOnClickListener(v -> chooseImage());
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        if (currentUser == null) return;

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    edtUsername.setText(user.getUsername());
                    edtEmail.setText(user.getEmail());

                    // Nếu có ảnh đại diện, tải ảnh vào ImageView
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(EditProfile.this).load(user.getAvatar()).into(imgAvatar);
                    }
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        }
    }

    private void saveUserData() {
        progressDialog.show();
        String username = edtUsername.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Tên không được để trống!");
            progressDialog.dismiss();
            return;
        }

        if (imageUri != null) {
            // Nếu có ảnh, chuyển ảnh sang Base64 và lưu vào Firebase Realtime Database
            try {
                String base64Image = encodeImageToBase64(imageUri);
                updateRealtimeDatabase(username, base64Image);
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "Lỗi tải ảnh!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Nếu không có ảnh, chỉ cập nhật thông tin người dùng
            updateRealtimeDatabase(username, null);
        }
    }

    private String encodeImageToBase64(Uri imageUri) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void updateRealtimeDatabase(String username, String base64Image) {
        if (currentUser == null) return;

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        // Nếu có ảnh Base64, lưu vào trong cơ sở dữ liệu
        if (base64Image != null) {
            updates.put("avatar", base64Image);
        }

        userRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Toast.makeText(EditProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(EditProfile.this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
        });
    }
}
