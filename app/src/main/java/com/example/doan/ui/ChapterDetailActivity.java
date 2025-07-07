package com.example.doan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChapterDetailActivity extends AppCompatActivity {
    private TextView tvChapterTitle, tvChapterContent;
    private ImageButton btnBack, btnPrevChapter, btnNextChapter;
    private String storyId, chapterId;
    private DatabaseReference databaseReference;
    private List<String> chapterIds; // Danh sách các ID chương
    private int currentChapterIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);

        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterContent = findViewById(R.id.tvChapterContent);
        btnBack = findViewById(R.id.btnBack);
        btnPrevChapter = findViewById(R.id.btnPrevChapter);
        btnNextChapter = findViewById(R.id.btnNextChapter);

        btnPrevChapter.setOnClickListener(v -> loadChapter(-1)); // Chuyển đến chương trước
        btnNextChapter.setOnClickListener(v -> loadChapter(1));  // Chuyển đến chương sau

        btnBack.setOnClickListener(view -> finish());  // Quay lại màn trước

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");
        chapterId = intent.getStringExtra("chapterId");

        // Kiểm tra dữ liệu đầu vào
        if (storyId == null || chapterId == null) {
            Toast.makeText(this, "Lỗi: Thiếu dữ liệu chương!", Toast.LENGTH_SHORT).show();
            finish();  // Quay lại nếu thiếu dữ liệu
            return;
        }

        // Lấy danh sách các chương từ Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("stories")
                .child(storyId).child("chapters");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chapterIds = new ArrayList<>();
                    for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                        chapterIds.add(chapterSnapshot.getKey());
                    }
                    // Tìm vị trí của chương hiện tại
                    currentChapterIndex = chapterIds.indexOf(chapterId);
                    loadChapter(0); // Tải chương hiện tại
                } else {
                    Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy chương!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChapterDetailActivity.this, "Lỗi tải danh sách chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChapter(int offset) {
        int newIndex = currentChapterIndex + offset;
        if (newIndex >= 0 && newIndex < chapterIds.size()) {
            currentChapterIndex = newIndex;
            chapterId = chapterIds.get(currentChapterIndex);
            databaseReference.child(chapterId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String title = snapshot.child("title").getValue(String.class);
                        String content = snapshot.child("content").getValue(String.class);
                        if (title != null && content != null) {
                            tvChapterTitle.setText(title);
                            tvChapterContent.setText(content);
                        } else {
                            Toast.makeText(ChapterDetailActivity.this, "Dữ liệu chương không đầy đủ!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChapterDetailActivity.this, "Chương không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChapterDetailActivity.this, "Lỗi tải dữ liệu chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
