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

        // Thêm sự kiện click cho ImageView để mở thư viện
        imgCover.setOnClickListener(v -> openGallery());

        // Khi nhập URL ảnh -> Hiển thị ngay
        edtImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                Log.d("EditStory", "URL nhập vào: " + url);

                if (!TextUtils.isEmpty(url) && imgCover != null) {
                    // Kiểm tra xem url có phải là một resource id hay không
                    if (TextUtils.isDigitsOnly(url)) { // Giả sử resource id là số
                        try {
                            int resId = Integer.parseInt(url);
                            Glide.with(EditStory.this).load(resId).into(imgCover);
                        } catch (NumberFormatException e) {
                             Glide.with(EditStory.this).load(url).into(imgCover); // Nếu không phải số thì coi như URL
                        }
                    } else if (url.startsWith("android.resource://") || url.startsWith("content://") || url.startsWith("file://")) {
                        Glide.with(EditStory.this).load(Uri.parse(url)).into(imgCover);
                    }
                    else {
                        // Cố gắng load như một tên drawable nếu không phải URL đầy đủ
                        int resId = getResources().getIdentifier(url, "drawable", getPackageName());
                        if (resId != 0) {
                            Glide.with(EditStory.this).load(resId).into(imgCover);
                        } else {
                             Log.e("EditStory", "Không tìm thấy ảnh trong drawable hoặc URL không hợp lệ: " + url);
                             // Có thể hiển thị một ảnh placeholder ở đây nếu muốn
                             // imgCover.setImageResource(R.drawable.placeholder_image);
                        }
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
                        edtImageUrl.setText(imageUrl); // Cập nhật EditText với Uri của ảnh được chọn
                    }
                }
        );
    }

    // Mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Chỉ chọn các loại file ảnh
        galleryLauncher.launch(intent);
    }

    private void loadStoryData(String storyId) {
        databaseReference.child(storyId).get().addOnSuccessListener(snapshot -> {
            Story story = snapshot.getValue(Story.class);
            if (story != null) {
                edtTitle.setText(story.getTitle());
                edtDescription.setText(story.getDescription());
                edtCategory.setText(story.getCategory());
                
                imageUrl = story.getImageResource(); // Gán imageUrl từ story
                edtImageUrl.setText(imageUrl); // Cập nhật EditText với imageUrl

                // Hiển thị ảnh bìa
                if (!TextUtils.isEmpty(imageUrl) && imgCover != null) {
                     if (imageUrl.startsWith("android.resource://") || imageUrl.startsWith("content://") || imageUrl.startsWith("file://") || imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                        Glide.with(this).load(Uri.parse(imageUrl)).into(imgCover);
                    } else {
                        int resId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
                        if (resId != 0) {
                            Glide.with(this).load(resId).into(imgCover);
                        } else {
                            Log.e("EditStory", "Không tìm thấy ảnh trong drawable: " + imageUrl);
                            // imgCover.setImageResource(R.drawable.placeholder_image); // Ảnh mặc định nếu không load được
                        }
                    }
                } else {
                    Log.e("EditStory", "imageUrl hoặc imgCover bị null khi loadStoryData");
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(EditStory.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveChanges() {
        String newTitle = edtTitle.getText().toString().trim();
        String newDescription = edtDescription.getText().toString().trim();
        String newCategory = edtCategory.getText().toString().trim();
        // Lấy URL ảnh từ edtImageUrl, vì nó có thể đã được cập nhật từ gallery hoặc nhập tay
        String finalImageUrl = edtImageUrl.getText().toString().trim();


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
        // Sử dụng finalImageUrl đã được cập nhật
        Story updatedStory = new Story(storyId, newTitle, newDescription, newCategory, finalImageUrl, type, userId, new HashMap<>());

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
