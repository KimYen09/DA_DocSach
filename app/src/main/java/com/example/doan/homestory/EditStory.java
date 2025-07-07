//package com.example.doan.homestory;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.Toast;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.doan.R;
//import com.example.doan.model.Chapter;
//import com.example.doan.model.Story;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class EditStory extends AppCompatActivity {
//    private EditText edtTitle, edtDescription;
//    private EditText edtCategory;
//    private ImageView imgCover;
//    private Button btnSave, btnChooseImage;
//    private DatabaseReference databaseReference;
//    private String storyId;
//    private String imageUrl; // Lưu URL ảnh mới
//    private ActivityResultLauncher<Intent> galleryLauncher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_story);
//
//        edtTitle = findViewById(R.id.edtTitle);
//        edtDescription = findViewById(R.id.edtDescription);
//        edtCategory = findViewById(R.id.edtCategory);
//        imgCover = findViewById(R.id.imgCover);
//        btnSave = findViewById(R.id.btnSave);
//        btnChooseImage = findViewById(R.id.btnChooseImage);
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
//
//        // Đăng ký ActivityResultLauncher để chọn ảnh
//        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        imgCover.setImageURI(imageUri);
//                        imageUrl = imageUri.toString(); // Lưu đường dẫn ảnh mới
//                    }
//                });
//
//        btnChooseImage.setOnClickListener(v -> openGallery());
//
//        // Nhận ID truyện
//        storyId = getIntent().getStringExtra("storyId");
//        if (storyId != null) {
//            loadStoryData(storyId);
//        }
//
//        btnSave.setOnClickListener(v -> saveChanges());
//    }
//
//    // Mở thư viện ảnh
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("drawable/*");
//        galleryLauncher.launch(intent);
//    }
//
//
//    // Mở thư viện ảnh
//    private void loadStoryData(String storyId) {
//        databaseReference.child(storyId).get().addOnSuccessListener(snapshot -> {
//            Story story = snapshot.getValue(Story.class);
//            if (story != null) {
//                edtTitle.setText(story.getTitle());
//                edtDescription.setText(story.getDescription());
//                edtCategory.setText(story.getCategory());
//                imageUrl = story.getImageResource();
//
//                // Hiển thị ảnh bìa
//                Glide.with(this).load(imageUrl).into(imgCover);
//            }
//        }).addOnFailureListener(e ->
//                Toast.makeText(EditStory.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
//        );
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            imgCover.setImageURI(imageUri);
//            imageUrl = imageUri.toString(); // Lưu đường dẫn ảnh mới
//        }
//    }
//
//
//    private void saveChanges() {
//        String newTitle = edtTitle.getText().toString().trim();
//        String newDescription = edtDescription.getText().toString().trim();
//        String newCategory = edtCategory.getText().toString().trim();
//
//        if (newTitle.isEmpty() || newDescription.isEmpty()) {
//            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Lấy authorId từ FirebaseAuth (tài khoản đang đăng nhập)
//        String authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // Lấy danh sách chapters từ Firebase trước khi cập nhật
//        databaseReference.child(storyId).child("chapters").get().addOnSuccessListener(snapshot -> {
//            Map<String, Chapter> chapters = new HashMap<>();
//            for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
//                Chapter chapter = chapterSnapshot.getValue(Chapter.class);
//                if (chapter != null) {
//                    chapters.put(chapterSnapshot.getKey(), chapter);
//                }
//            }
//
//            // Cập nhật truyện
//            Story updatedStory = new Story(storyId, newTitle, newDescription, newCategory, imageUrl, authorId, chapters);
//            databaseReference.child(storyId).setValue(updatedStory)
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(EditStory.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
//                        finish();
//                    })
//                    .addOnFailureListener(e ->
//                            Toast.makeText(EditStory.this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
//                    );
//
//        }).addOnFailureListener(e ->
//                Toast.makeText(EditStory.this, "Lỗi tải chương truyện!", Toast.LENGTH_SHORT).show()
//        );
//    }
//
//
//}

package com.example.doan.homestory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditStory extends AppCompatActivity {
    private EditText edtTitle, edtDescription, edtCategory, edtImageUrl;
    private Button btnSave;
    private ImageView imgCover;
    private ImageButton btnBack;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private RadioGroup radioGroupType;
    private RadioButton radioSelected, radioPremium;
    private String storyId, imageUrl;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtCategory = findViewById(R.id.edtCategory);
        edtImageUrl = findViewById(R.id.editPic);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        imgCover = findViewById(R.id.imgCover);
        radioGroupType = findViewById(R.id.radioGroupType);
        radioSelected = findViewById(R.id.radioSelected);
        radioPremium = findViewById(R.id.radioPremium);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Lấy ID truyện từ Intent
        storyId = getIntent().getStringExtra("storyId");
        if (storyId != null) {
            loadStoryData(storyId);
        }

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());

        // Khi nhập URL ảnh -> Hiển thị ngay
        edtImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                Log.d("EditStory", "URL nhập vào: " + url);

                if (!TextUtils.isEmpty(url) && imgCover != null) {
                    int resId = getResources().getIdentifier(url, "drawable", getPackageName());
                    if (resId != 0) {
                        Glide.with(EditStory.this).load(resId).into(imgCover);
                    } else {
                        Log.e("EditStory", "Không tìm thấy ảnh trong drawable: " + url);
                    }
                } else {
                    Log.e("EditStory", "imgCover hoặc URL ảnh bị null");
                }
            }


            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Khởi tạo ActivityResultLauncher để chọn ảnh từ thư viện
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imgCover.setImageURI(imageUri);
                        imageUrl = imageUri.toString();
                        edtImageUrl.setText(imageUrl); // Cập nhật EditText
                    }
                }
        );
    }

    private void loadStoryData(String storyId) {
        databaseReference.child(storyId).get().addOnSuccessListener(snapshot -> {
            Story story = snapshot.getValue(Story.class);
            if (story != null) {
                edtTitle.setText(story.getTitle());
                edtDescription.setText(story.getDescription());
                edtCategory.setText(story.getCategory());
                if (!TextUtils.isEmpty(imageUrl) && imgCover != null) {
                    int resId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
                    if (resId != 0) {
                        Glide.with(this).load(resId).into(imgCover);
                    } else {
                        Glide.with(this).load(imageUrl).into(imgCover); // Nếu là URL online
                    }
                } else {
                    Log.e("EditStory", "imageUrl hoặc imgCover bị null");
                }


                imageUrl = story.getImageResource();
                Glide.with(this).load(imageUrl).into(imgCover);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(EditStory.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveChanges() {
        String newTitle = edtTitle.getText().toString().trim();
        String newDescription = edtDescription.getText().toString().trim();
        String newCategory = edtCategory.getText().toString().trim();
        String newImageUrl = edtImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newCategory)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String type = radioSelected.isChecked() ? "Tuyển chọn" : "Premium";
        Story updatedStory = new Story(storyId, newTitle, newDescription, newCategory, newImageUrl, type, userId, new HashMap<>());

        databaseReference.child(storyId).setValue(updatedStory)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
