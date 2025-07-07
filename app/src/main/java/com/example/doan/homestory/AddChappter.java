package com.example.doan.homestory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.fragmenthome.WriteFragment;
import com.example.doan.model.Chapter;
import com.example.doan.model.Chapters;
import com.example.doan.ui.ChapterDetailActivity;
import com.example.doan.ui.ChapterListActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddChappter extends AppCompatActivity {
    private EditText edtChapterTitle, edtChapterContent;
    private Button btnAddChapter;
    private DatabaseReference databaseReference;
    ImageButton btnBack;
    private String storyId; // ID của truyện cần thêm chương

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chappter); // 🛑 Đổi tên file XML cho đúng

        edtChapterTitle = findViewById(R.id.edtChapterTitle);
        edtChapterContent = findViewById(R.id.edtChapterContent);
        btnAddChapter = findViewById(R.id.btnAddChapter);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> {
            finish(); // 🔥 Quay lại màn trước đó
        });


        // Nhận storyId từ Intent
        storyId = getIntent().getStringExtra("storyId");
        if (storyId == null) {
            Toast.makeText(this, "Không tìm thấy truyện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("stories").child(storyId).child("chapters");

        btnAddChapter.setOnClickListener(v -> addChapter());
    }

    private void addChapter() {
        String chapterTitle = edtChapterTitle.getText().toString().trim();
        String chapterContent = edtChapterContent.getText().toString().trim();

        if (TextUtils.isEmpty(chapterTitle) || TextUtils.isEmpty(chapterContent)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String chapterId = databaseReference.push().getKey();
        if (chapterId == null) {
            Toast.makeText(this, "Không thể tạo ID chương!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 Thêm storyId vào object Chapter
        Chapter chapter = new Chapter(chapterId, chapterTitle, chapterContent, storyId);

        databaseReference.child(chapterId).setValue(chapter)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm chương thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


}
