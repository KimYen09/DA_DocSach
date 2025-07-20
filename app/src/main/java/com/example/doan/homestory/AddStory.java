//
//package com.example.doan.homestory;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
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
//public class AddStory extends AppCompatActivity {
//    private EditText edtTitle, edtDescription, edtCategory, edtImageUrl;
//    private Button btnAddStory;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth auth;
//    private RadioGroup radioGroupType;
//    private ImageView img;
//
//    private RadioButton radioSelected, radioPremium;
//
//    ImageButton btnBack;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_story);
//
//        edtTitle = findViewById(R.id.edtTitle);
//        edtDescription = findViewById(R.id.edtDescription);
//        edtCategory = findViewById(R.id.edtCategory);
//        edtImageUrl = findViewById(R.id.edtImageUrl);
//        btnAddStory = findViewById(R.id.btnAddStory);
//        btnBack = findViewById(R.id.btnBack);
//        radioGroupType = findViewById(R.id.radioGroupType);
//        radioSelected = findViewById(R.id.radioSelected);
//        radioPremium = findViewById(R.id.radioPremium);
//        img = findViewById(R.id.img);
//
//
//        btnBack.setOnClickListener(view -> {
//            finish(); // 🔥 Quay lại màn trước đó
//        });
//
//
//        edtImageUrl.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String url = s.toString().trim();
//                Log.d("EditStory", "URL nhập vào: " + url);
//
//                if (!TextUtils.isEmpty(url) && img != null) {
//                    int resId = getResources().getIdentifier(url, "drawable", getPackageName());
//                    if (resId != 0) {
//                        Glide.with(AddStory.this).load(resId).into(img);
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
//        auth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
//
//        btnAddStory.setOnClickListener(v -> addStory());
//    }
//
////    private void addStory() {
////        String title = edtTitle.getText().toString().trim();
////        String description = edtDescription.getText().toString().trim();
////        String category = edtCategory.getText().toString().trim();
////        String imageName = edtImageUrl.getText().toString().trim();
////
////        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
////            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        if (auth.getCurrentUser() == null) {
////            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        String userId = auth.getCurrentUser().getUid();
////        String storyId = databaseReference.push().getKey();
////
////        if (storyId == null) {
////            Toast.makeText(this, "Không thể tạo ID!", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        String type = radioSelected.isChecked() ? "Tuyển chọn" : "Premium";
////        Log.d("CheckType", "Giá trị type trước khi lưu: " + type);
////
////        Story story = new Story(storyId, title, description, category, imageName, userId, type, new HashMap<>());
////
////        databaseReference.child(storyId).setValue(story)
////                .addOnSuccessListener(aVoid -> {
////                    Toast.makeText(this, "Thêm truyện thành công!", Toast.LENGTH_SHORT).show();
////                    finish();
////                })
////                .addOnFailureListener(e ->
////                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
////                );
////
////
////    }
//
//
//    private void addStory() {
//        String title = edtTitle.getText().toString().trim();
//        String description = edtDescription.getText().toString().trim();
//        String category = edtCategory.getText().toString().trim();
//        String imageName = edtImageUrl.getText().toString().trim();
//
//        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
//            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (auth.getCurrentUser() == null) {
//            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String storyId = databaseReference.push().getKey();
//
//        String type ;
//        if (radioSelected.isChecked()) {
//            type = "Tuyển chọn";
//        } else if (radioPremium.isChecked()) {
//            type = "Premium";
//        } else {
//            type = "Khác";
//        }
//
//
//        if (storyId == null) {
//            Toast.makeText(this, "Không thể tạo ID!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        String userId = auth.getCurrentUser().getUid();
//        Story story = new Story(storyId, title, description, category, imageName, type, userId, new HashMap<>());
//
//        databaseReference.child(storyId).setValue(story)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Thêm truyện thành công!", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//    }
//
//
//
//}

package com.example.doan.homestory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat; // Thêm import này
import java.util.Date; // Thêm import này
import java.util.HashMap;
import java.util.Locale; // Thêm import này

public class AddStory extends AppCompatActivity {
    private EditText edtTitle, edtDescription, edtCategory, edtImageUrl;
    private Button btnAddStory;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private RadioGroup radioGroupType;
    private ImageView img;

    private RadioButton radioSelected, radioPremium;

    ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtCategory = findViewById(R.id.edtCategory);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        btnAddStory = findViewById(R.id.btnAddStory);
        btnBack = findViewById(R.id.btnBack);
        radioGroupType = findViewById(R.id.radioGroupType);
        radioSelected = findViewById(R.id.radioSelected);
        radioPremium = findViewById(R.id.radioPremium);
        img = findViewById(R.id.img);


        btnBack.setOnClickListener(view -> {
            finish(); // 🔥 Quay lại màn trước đó
        });


        edtImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                Log.d("EditStory", "URL nhập vào: " + url);

                if (!TextUtils.isEmpty(url) && img != null) {
                    int resId = getResources().getIdentifier(url, "drawable", getPackageName());
                    if (resId != 0) {
                        Glide.with(AddStory.this).load(resId).into(img);
                    } else {
                        Log.e("EditStory", "Không tìm thấy ảnh trong drawable: " + url);
                        // Có thể đặt ảnh placeholder nếu không tìm thấy
                        img.setImageResource(R.drawable.lgsach2); // Ví dụ: ảnh lỗi hoặc mặc định
                    }
                } else {
                    Log.e("EditStory", "imgCover hoặc URL ảnh bị null/rỗng");
                    // Đặt ảnh placeholder nếu URL rỗng hoặc null
                    img.setImageResource(R.drawable.lgsach2); // Ví dụ: ảnh lỗi hoặc mặc định
                }
            }


            @Override
            public void afterTextChanged(Editable s) {}
        });
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        btnAddStory.setOnClickListener(v -> addStory());
    }

    private void addStory() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String imageName = edtImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String storyId = databaseReference.push().getKey();

        String type ;
        if (radioSelected.isChecked()) {
            type = "Tuyển chọn";
        } else if (radioPremium.isChecked()) {
            type = "Premium";
        } else {
            // Nếu không có RadioButton nào được chọn, đặt một giá trị mặc định hoặc báo lỗi
            Toast.makeText(this, "Vui lòng chọn loại truyện (Tuyển chọn/Premium)!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (storyId == null) {
            Toast.makeText(this, "Không thể tạo ID truyện!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Lấy ngày hiện tại và định dạng thành "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String creationDate = sdf.format(new Date());

        // Tạo đối tượng Story với creationDate
        // Đảm bảo constructor của Story model có thể nhận creationDate
        Story story = new Story(storyId, title, description, category, imageName, type, userId, new HashMap<>(), creationDate);

        databaseReference.child(storyId).setValue(story)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm truyện thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
