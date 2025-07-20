// File: app/src/main/java/com/example/doan/homestory/EditChapterActivity.java
package com.example.doan.adminactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.model.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditChapters extends AppCompatActivity {

    private static final String TAG = "EditChapterActivity";

    private EditText edtChapterTitle;
    private EditText edtChapterContent;
    private Button btnSaveChapter;
    private ImageButton btnBack;

    private String storyId;
    private String chapterId;
    private DatabaseReference chapterRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapters);

        // Ánh xạ View
        edtChapterTitle = findViewById(R.id.edtChapterTitle);
        edtChapterContent = findViewById(R.id.edtChapterContent);
        btnSaveChapter = findViewById(R.id.btnSaveChapter);
        btnBack = findViewById(R.id.btnBack);

        // Lấy storyId và chapterId từ Intent
        storyId = getIntent().getStringExtra("storyId");
        chapterId = getIntent().getStringExtra("chapterId");

        if (storyId == null || storyId.isEmpty() || chapterId == null || chapterId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin chương.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tham chiếu Firebase đến chương cụ thể
        chapterRef = FirebaseDatabase.getInstance().getReference("stories")
                .child(storyId)
                .child("chapters")
                .child(chapterId);

        loadChapterData(); // Tải dữ liệu chương hiện có

        btnBack.setOnClickListener(v -> finish());
        btnSaveChapter.setOnClickListener(v -> saveChapterChanges());
    }

    private void loadChapterData() {
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Firebase có thể lưu chương dưới dạng Map<String, Object>
                // hoặc là một đối tượng Chapter đầy đủ
                // Cần điều chỉnh tùy theo cách bạn lưu trữ chương
                Chapter chapter = snapshot.getValue(Chapter.class); // Thử ánh xạ trực tiếp
                if (chapter != null) {
                    edtChapterTitle.setText(chapter.getTitle());
                    edtChapterContent.setText(chapter.getContent());
                } else {
                    // Nếu Chapter không ánh xạ trực tiếp, có thể là Map
                    // Ví dụ: chapters là Map<String, Boolean> hoặc Map<String, String>
                    // Bạn cần điều chỉnh cách đọc dữ liệu ở đây
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    edtChapterTitle.setText(title != null ? title : "Chương " + chapterId);
                    edtChapterContent.setText(content != null ? content : "");
                    Log.w(TAG, "Chapter object is null, trying to read fields directly.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditChapters.this, "Lỗi tải dữ liệu chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi Firebase khi tải dữ liệu chương: " + error.getMessage());
                finish();
            }
        });
    }

    private void saveChapterChanges() {
        String newTitle = edtChapterTitle.getText().toString().trim();
        String newContent = edtChapterContent.getText().toString().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            Toast.makeText(this, "Tiêu đề và nội dung chương không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Map để cập nhật các trường cụ thể
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", newTitle);
        updates.put("content", newContent);

        chapterRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditChapters.this, "Cập nhật chương thành công!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK); // Đặt kết quả OK để ChapterEditListActivity biết đã có thay đổi
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditChapters.this, "Lỗi cập nhật chương: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi Firebase khi cập nhật chương: " + e.getMessage());
                });
    }
}