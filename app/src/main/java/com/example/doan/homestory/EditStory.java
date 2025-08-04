////package com.example.doan.homestory;
////
////import android.app.Activity;
////import android.content.Intent;
////import android.net.Uri;
////import android.os.Bundle;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.ImageView;
////import android.widget.Spinner;
////import android.widget.Toast;
////import androidx.activity.result.ActivityResultLauncher;
////import androidx.activity.result.contract.ActivityResultContracts;
////
////import androidx.annotation.Nullable;
////import androidx.appcompat.app.AppCompatActivity;
////
////import com.bumptech.glide.Glide;
////import com.example.doan.R;
////import com.example.doan.model.Chapter;
////import com.example.doan.model.Story;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////
////import java.util.HashMap;
////import java.util.Map;
////
////public class EditStory extends AppCompatActivity {
////    private EditText edtTitle, edtDescription;
////    private EditText edtCategory;
////    private ImageView imgCover;
////    private Button btnSave, btnChooseImage;
////    private DatabaseReference databaseReference;
////    private String storyId;
////    private String imageUrl; // Lưu URL ảnh mới
////    private ActivityResultLauncher<Intent> galleryLauncher;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_edit_story);
////
////        edtTitle = findViewById(R.id.edtTitle);
////        edtDescription = findViewById(R.id.edtDescription);
////        edtCategory = findViewById(R.id.edtCategory);
////        imgCover = findViewById(R.id.imgCover);
////        btnSave = findViewById(R.id.btnSave);
////        btnChooseImage = findViewById(R.id.btnChooseImage);
////
////        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
////
////        // Đăng ký ActivityResultLauncher để chọn ảnh
////        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
////                result -> {
////                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
////                        Uri imageUri = result.getData().getData();
////                        imgCover.setImageURI(imageUri);
////                        imageUrl = imageUri.toString(); // Lưu đường dẫn ảnh mới
////                    }
////                });
////
////        btnChooseImage.setOnClickListener(v -> openGallery());
////
////        // Nhận ID truyện
////        storyId = getIntent().getStringExtra("storyId");
////        if (storyId != null) {
////            loadStoryData(storyId);
////        }
////
////        btnSave.setOnClickListener(v -> saveChanges());
////    }
////
////    // Mở thư viện ảnh
////    private void openGallery() {
////        Intent intent = new Intent(Intent.ACTION_PICK);
////        intent.setType("drawable/*");
////        galleryLauncher.launch(intent);
////    }
////
////
////    // Mở thư viện ảnh
////    private void loadStoryData(String storyId) {
////        databaseReference.child(storyId).get().addOnSuccessListener(snapshot -> {
////            Story story = snapshot.getValue(Story.class);
////            if (story != null) {
////                edtTitle.setText(story.getTitle());
////                edtDescription.setText(story.getDescription());
////                edtCategory.setText(story.getCategory());
////                imageUrl = story.getImageResource();
////
////                // Hiển thị ảnh bìa
////                Glide.with(this).load(imageUrl).into(imgCover);
////            }
////        }).addOnFailureListener(e ->
////                Toast.makeText(EditStory.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
////        );
////    }
////
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
////            Uri imageUri = data.getData();
////            imgCover.setImageURI(imageUri);
////            imageUrl = imageUri.toString(); // Lưu đường dẫn ảnh mới
////        }
////    }
////
////
////    private void saveChanges() {
////        String newTitle = edtTitle.getText().toString().trim();
////        String newDescription = edtDescription.getText().toString().trim();
////        String newCategory = edtCategory.getText().toString().trim();
////
////        if (newTitle.isEmpty() || newDescription.isEmpty()) {
////            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        // Lấy authorId từ FirebaseAuth (tài khoản đang đăng nhập)
////        String authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////
////        // Lấy danh sách chapters từ Firebase trước khi cập nhật
////        databaseReference.child(storyId).child("chapters").get().addOnSuccessListener(snapshot -> {
////            Map<String, Chapter> chapters = new HashMap<>();
////            for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
////                Chapter chapter = chapterSnapshot.getValue(Chapter.class);
////                if (chapter != null) {
////                    chapters.put(chapterSnapshot.getKey(), chapter);
////                }
////            }
////
////            // Cập nhật truyện
////            Story updatedStory = new Story(storyId, newTitle, newDescription, newCategory, imageUrl, authorId, chapters);
////            databaseReference.child(storyId).setValue(updatedStory)
////                    .addOnSuccessListener(aVoid -> {
////                        Toast.makeText(EditStory.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
////                        finish();
////                    })
////                    .addOnFailureListener(e ->
////                            Toast.makeText(EditStory.this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
////                    );
////
////        }).addOnFailureListener(e ->
////                Toast.makeText(EditStory.this, "Lỗi tải chương truyện!", Toast.LENGTH_SHORT).show()
////        );
////    }
////
////
////}
//
//package com.example.doan.homestory;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.text.Editable;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.doan.R;
//import com.example.doan.model.Story;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//
//public class EditStory extends AppCompatActivity {
//    private EditText edtTitle, edtDescription, edtCategory, edtImageUrl;
//    private Button btnSave;
//    private ImageView imgCover;
//    private ImageButton btnBack;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth auth;
//    private RadioGroup radioGroupType;
//    private RadioButton radioSelected, radioPremium;
//    private String storyId, imageUrl;
//
//    private ActivityResultLauncher<Intent> galleryLauncher;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_story);
//
//        edtTitle = findViewById(R.id.edtTitle);
//        edtDescription = findViewById(R.id.edtDescription);
//        edtCategory = findViewById(R.id.edtCategory);
//        edtImageUrl = findViewById(R.id.editPic);
//        btnSave = findViewById(R.id.btnSave);
//        btnBack = findViewById(R.id.btnBack);
//        imgCover = findViewById(R.id.imgCover);
//        radioGroupType = findViewById(R.id.radioGroupType);
//        radioSelected = findViewById(R.id.radioSelected);
//        radioPremium = findViewById(R.id.radioPremium);
//
//
//        auth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
//
//        // Lấy ID truyện từ Intent
//        storyId = getIntent().getStringExtra("storyId");
//        if (storyId != null) {
//            loadStoryData(storyId);
//        }
//
//        btnBack.setOnClickListener(v -> finish());
//        btnSave.setOnClickListener(v -> saveChanges());
//
//        // Khi nhập URL ảnh -> Hiển thị ngay
//        edtImageUrl.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String url = s.toString().trim();
//                Log.d("EditStory", "URL nhập vào: " + url);
//
//                if (!TextUtils.isEmpty(url) && imgCover != null) {
//                    int resId = getResources().getIdentifier(url, "drawable", getPackageName());
//                    if (resId != 0) {
//                        Glide.with(EditStory.this).load(resId).into(imgCover);
//                    } else {
//                        Log.e("EditStory", "Không tìm thấy ảnh trong drawable: " + url);
//                    }
//                } else {
//                    Log.e("EditStory", "imgCover hoặc URL ảnh bị null");
//                }
//            }
//
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        // Khởi tạo ActivityResultLauncher để chọn ảnh từ thư viện
//        galleryLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        imgCover.setImageURI(imageUri);
//                        imageUrl = imageUri.toString();
//                        edtImageUrl.setText(imageUrl); // Cập nhật EditText
//                    }
//                }
//        );
//    }
//
//    private void loadStoryData(String storyId) {
//        databaseReference.child(storyId).get().addOnSuccessListener(snapshot -> {
//            Story story = snapshot.getValue(Story.class);
//            if (story != null) {
//                edtTitle.setText(story.getTitle());
//                edtDescription.setText(story.getDescription());
//                edtCategory.setText(story.getCategory());
//                if (!TextUtils.isEmpty(imageUrl) && imgCover != null) {
//                    int resId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
//                    if (resId != 0) {
//                        Glide.with(this).load(resId).into(imgCover);
//                    } else {
//                        Glide.with(this).load(imageUrl).into(imgCover); // Nếu là URL online
//                    }
//                } else {
//                    Log.e("EditStory", "imageUrl hoặc imgCover bị null");
//                }
//
//
//                imageUrl = story.getImageResource();
//                Glide.with(this).load(imageUrl).into(imgCover);
//            }
//        }).addOnFailureListener(e ->
//                Toast.makeText(EditStory.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
//        );
//    }
//
//    private void saveChanges() {
//        String newTitle = edtTitle.getText().toString().trim();
//        String newDescription = edtDescription.getText().toString().trim();
//        String newCategory = edtCategory.getText().toString().trim();
//        String newImageUrl = edtImageUrl.getText().toString().trim();
//
//        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newCategory)) {
//            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (auth.getCurrentUser() == null) {
//            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = auth.getCurrentUser().getUid();
//        String type = radioSelected.isChecked() ? "Tuyển chọn" : "Premium";
//        Story updatedStory = new Story(storyId, newTitle, newDescription, newCategory, newImageUrl, type, userId, new HashMap<>(), creationDate);
//
//        databaseReference.child(storyId).setValue(updatedStory)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//    }
//}

package com.example.doan.homestory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View; // Import View
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat; // Thêm import này
import java.util.Date; // Thêm import này
import java.util.HashMap;
import java.util.Locale; // Thêm import này
import java.util.Map; // Thêm import này

public class EditStory extends AppCompatActivity {
    private EditText edtTitle, edtDescription, edtCategory, edtImageUrl;
    private Button btnSave;
    private ImageView imgCover;
    private ImageButton btnBack;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private RadioGroup radioGroupType;
    private RadioButton radioSelected, radioPremium;
    private String storyId;
    private String currentImageUrl; // Đổi tên để tránh nhầm lẫn với imageUrl từ gallery
    private String creationDate; // Khai báo biến creationDate

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        // Ánh xạ các View
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtCategory = findViewById(R.id.edtCategory);
        edtImageUrl = findViewById(R.id.editPic); // Đảm bảo ID này đúng trong layout
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
        } else {
            Toast.makeText(this, "Không tìm thấy ID truyện để chỉnh sửa.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ID
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
                currentImageUrl = url; // Cập nhật currentImageUrl ngay khi nhập

                if (!TextUtils.isEmpty(url) && imgCover != null) {
                    int resId = getResources().getIdentifier(url, "drawable", getPackageName());
                    if (resId != 0) {
                        Glide.with(EditStory.this).load(resId).into(imgCover);
                    } else {
                        // Thử tải từ URL nếu không phải drawable
                        Glide.with(EditStory.this).load(url)
                                .placeholder(R.drawable.lgsach2) // Ảnh placeholder khi đang tải
                                .error(R.drawable.lgsach2) // Ảnh lỗi nếu không tải được
                                .into(imgCover);
                        Log.e("EditStory", "Không tìm thấy ảnh trong drawable, thử tải từ URL: " + url);
                    }
                } else if (imgCover != null) {
                    imgCover.setImageResource(R.drawable.lgsach2); // Đặt ảnh mặc định nếu URL rỗng
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
                        currentImageUrl = imageUri.toString(); // Lưu đường dẫn URI mới
                        edtImageUrl.setText(currentImageUrl); // Cập nhật EditText với URI
                    }
                }
        );

        // Thêm nút chọn ảnh nếu bạn có nó trong layout activity_edit_story.xml
//        // Ví dụ: <Button android:id="@+id/btnChooseImage" ... />
//        Button btnChooseImage = findViewById(R.id.btnChooseImage); // Ánh xạ nút chọn ảnh
//        if (btnChooseImage != null) {
//            btnChooseImage.setOnClickListener(v -> openGallery());
//        }
    }

    // Mở thư viện ảnh (đã sửa kiểu intent)
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Chọn tất cả các loại ảnh
        galleryLauncher.launch(intent);
    }

    private void loadStoryData(String storyId) {
        databaseReference.child(storyId).get().addOnSuccessListener(snapshot -> {
            Story story = snapshot.getValue(Story.class);
            if (story != null) {
                edtTitle.setText(story.getTitle());
                edtDescription.setText(story.getDescription());
                edtCategory.setText(story.getCategory());
                edtImageUrl.setText(story.getImageResource()); // Hiển thị đường dẫn ảnh trong EditText

                // Gán creationDate từ Firebase
                this.creationDate = story.getCreationDate();
                if (this.creationDate == null) {
                    // Nếu creationDate chưa có, đặt ngày hiện tại làm mặc định
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    this.creationDate = sdf.format(new Date());
                }

                // Thiết lập RadioButton dựa trên loại truyện
                if ("Tuyển chọn".equalsIgnoreCase(story.getType())) {
                    radioSelected.setChecked(true);
                } else if ("Premium".equalsIgnoreCase(story.getType())) {
                    radioPremium.setChecked(true);
                }

                // Hiển thị ảnh bìa
                currentImageUrl = story.getImageResource(); // Cập nhật currentImageUrl
                if (!TextUtils.isEmpty(currentImageUrl) && imgCover != null) {
                    int resId = getResources().getIdentifier(currentImageUrl, "drawable", getPackageName());
                    if (resId != 0) {
                        Glide.with(this).load(resId).into(imgCover);
                    } else {
                        Glide.with(this).load(currentImageUrl)
                                .placeholder(R.drawable.lgsach2)
                                .error(R.drawable.lgsach2)
                                .into(imgCover);
                    }
                } else if (imgCover != null) {
                    imgCover.setImageResource(R.drawable.lgsach2); // Ảnh mặc định nếu không có ảnh
                }

            } else {
                Toast.makeText(EditStory.this, "Không tìm thấy dữ liệu truyện.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditStory.this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("EditStory", "Lỗi tải dữ liệu truyện: " + e.getMessage());
            finish();
        });
    }

    private void saveChanges() {
        String newTitle = edtTitle.getText().toString().trim();
        String newDescription = edtDescription.getText().toString().trim();
        String newCategory = edtCategory.getText().toString().trim();
        // Lấy ảnh từ biến currentImageUrl đã được cập nhật từ EditText hoặc Gallery
        String finalImageUrl = currentImageUrl;

        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newCategory)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String type;
        if (radioSelected.isChecked()) {
            type = "Tuyển chọn";
        } else if (radioPremium.isChecked()) {
            type = "Premium";
        } else {
            Toast.makeText(this, "Vui lòng chọn loại truyện!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy chapters hiện có từ Firebase trước khi cập nhật toàn bộ Story
        databaseReference.child(storyId).child("chapters").get().addOnSuccessListener(snapshot -> {
            HashMap<String, Boolean> chapters = new HashMap<>();
            for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                // Giả định chapters lưu dưới dạng Map<String, Boolean> hoặc Map<String, Object>
                // Nếu là Map<String, Chapter>, bạn cần đọc từng Chapter object
                // Ví dụ: if (chapterSnapshot.getValue(Boolean.class) != null) {
                //             chapters.put(chapterSnapshot.getKey(), chapterSnapshot.getValue(Boolean.class));
                //         }
                // Nếu chapters của bạn là một Map đơn giản, đoạn này có thể cần điều chỉnh
                // Dòng này giả định chapters là Map<String, Object> hoặc Map<String, Boolean>
                chapters.put(chapterSnapshot.getKey(), true); // Hoặc giá trị thực của chapter
            }

            // Tạo đối tượng Story đã cập nhật, bao gồm creationDate
            // Đảm bảo constructor của Story model có thể nhận creationDate
            Story updatedStory = new Story(storyId, newTitle, newDescription, newCategory, finalImageUrl, type, userId, new HashMap<>(), creationDate);

            databaseReference.child(storyId).setValue(updatedStory)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditStory.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditStory.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("EditStory", "Lỗi khi cập nhật truyện: " + e.getMessage());
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(EditStory.this, "Lỗi tải chương truyện để cập nhật!", Toast.LENGTH_SHORT).show();
            Log.e("EditStory", "Lỗi tải chương truyện: " + e.getMessage());
        });
    }
}
