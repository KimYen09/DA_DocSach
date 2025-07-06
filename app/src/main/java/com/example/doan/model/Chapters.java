package com.example.doan.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Chapters extends AppCompatActivity {

    private TextView tvChapterTitle, tvChapterContent;
    private Button btnPrevChapter, btnNextChapter, btnBack;
    private DatabaseReference chapterRef;
    private String storyId;
    private int currentChapter = 1; // Chương hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        // Ánh xạ view
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterContent = findViewById(R.id.tvChapterContent);
        btnPrevChapter = findViewById(R.id.btnPrevChapter);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnBack = findViewById(R.id.btnBack);

        // Lấy storyId từ Intent
        storyId = getIntent().getStringExtra("storyId");
        currentChapter = getIntent().getIntExtra("chapterNumber", 1);

        // Truy vấn dữ liệu Firebase
        chapterRef = FirebaseDatabase.getInstance().getReference("stories").child(storyId).child("chapters");

        // Hiển thị chương đầu tiên
        loadChapter(currentChapter);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Chương trước
        btnPrevChapter.setOnClickListener(v -> {
            if (currentChapter > 1) {
                currentChapter--;
                loadChapter(currentChapter);
            } else {
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Chương tiếp theo
        btnNextChapter.setOnClickListener(v -> {
            currentChapter++;
            loadChapter(currentChapter);
        });
    }

    private void loadChapter(int chapterNumber) {
        chapterRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(Chapters.this, "Không có chương nào!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int index = 1;
                for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                    if (index == chapterNumber) {
                        String title = chapterSnapshot.child("title").getValue(String.class);
                        String content = chapterSnapshot.child("content").getValue(String.class);

                        tvChapterTitle.setText(title != null ? title : "Không có tiêu đề");
                        tvChapterContent.setText(content != null ? content : "Không có nội dung");
                        return;
                    }
                    index++;
                }

                Toast.makeText(Chapters.this, "Chương này không tồn tại", Toast.LENGTH_SHORT).show();
                currentChapter--; // Quay lại chương trước nếu chương không tồn tại
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chapters.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
