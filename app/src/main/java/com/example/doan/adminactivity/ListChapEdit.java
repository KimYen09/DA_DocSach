package com.example.doan.adminactivity;

import android.app.Activity;
import android.app.AlertDialog; // Sử dụng AlertDialog từ android.app
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton; // Import ImageButton
import android.widget.ImageView; // Import ImageView
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide
import com.example.doan.R;
import com.example.doan.adapter.ChapterAdapterEdit;
import com.example.doan.homestory.AddChappter;
import com.example.doan.model.Chapter;
import com.example.doan.model.Story; // Import Story model
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListChapEdit extends AppCompatActivity {

    private static final String TAG = "ChapterEditListActivity";
    private static final int EDIT_CHAPTER_REQUEST = 101; // Request code cho EditChapterActivity

    private RecyclerView recyclerViewChapters;
    private ChapterAdapterEdit chapterAdapter;
    private List<Chapter> chapterList;
    private ProgressBar progressBarChapters;
    private FloatingActionButton fabAddChapter;
    private TextView tvChapterListTitleHeader; // Tiêu đề màn hình
    private ImageButton btnBack; // Nút quay lại

    // UI Elements cho thông tin truyện
    private ImageView ivStoryCover;
    private TextView tvStoryDetailTitle;
    private TextView tvStoryDetailGenre;
    private TextView tvStoryDetailDescription;

    private String storyId; // ID của truyện mà chúng ta đang chỉnh sửa chương
    private DatabaseReference storiesRef; // Tham chiếu đến node "stories"
    private DatabaseReference chaptersRef; // Tham chiếu đến node chương của truyện

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chap_edit);

        // Ánh xạ View từ header
        btnBack = findViewById(R.id.btnBack);
        tvChapterListTitleHeader = findViewById(R.id.tvChapterListTitleHeader);

        // Ánh xạ View cho thông tin truyện
        ivStoryCover = findViewById(R.id.ivStoryCover);
        tvStoryDetailTitle = findViewById(R.id.tvStoryDetailTitle);
        tvStoryDetailGenre = findViewById(R.id.tvStoryDetailGenre);
        tvStoryDetailDescription = findViewById(R.id.tvStoryDetailDescription);

        // Ánh xạ View cho danh sách chương
        recyclerViewChapters = findViewById(R.id.recyclerViewChapters);
        progressBarChapters = findViewById(R.id.progressBarChapters);
        fabAddChapter = findViewById(R.id.fabAddChapter);

        // Lấy storyId từ Intent
        storyId = getIntent().getStringExtra("storyId");
        if (storyId == null || storyId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID truyện.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cấu hình RecyclerView
        recyclerViewChapters.setLayoutManager(new LinearLayoutManager(this));
        chapterList = new ArrayList<>();

        // Khởi tạo Adapter cho chương
        // Bạn sẽ cần tạo ChapterAdapter và Chapter model
        chapterAdapter = new ChapterAdapterEdit(this, chapterList, new ChapterAdapterEdit.OnChapterEditActionListener() {
            @Override
            public void onChapterClick(Chapter chapter) {
                // Khi click vào toàn bộ item chương (có thể mở màn hình đọc chương)
                Toast.makeText(ListChapEdit.this, "Xem chương: " + chapter.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Mở ChapterContentDisplayActivity
            }

            @Override
            public void onDeleteChapter(String chapterId) {
                deleteChapter(chapterId);
            }

            @Override
            public void onEditChapter(Chapter chapter) {
                Toast.makeText(ListChapEdit.this, "Chỉnh sửa chương: " + chapter.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListChapEdit.this, EditChapters.class);
                intent.putExtra("storyId", storyId);
                intent.putExtra("chapterId", chapter.getId());
                startActivityForResult(intent, EDIT_CHAPTER_REQUEST);
            }
        });
        recyclerViewChapters.setAdapter(chapterAdapter);

        // Tham chiếu Firebase
        storiesRef = FirebaseDatabase.getInstance().getReference("stories");
        chaptersRef = storiesRef.child(storyId).child("chapters"); // Tham chiếu đến node chương của truyện

        // Tải thông tin chi tiết truyện và danh sách chương
        loadStoryDetails();
        loadChapters();

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút thêm chương mới
        fabAddChapter.setOnClickListener(v -> {
            Intent intent = new Intent(ListChapEdit.this, AddChappter.class);
            intent.putExtra("storyId", storyId); // Truyền storyId
            startActivity(intent);
        });
    }

    /**
     * Tải thông tin chi tiết của truyện từ Firebase và hiển thị lên UI.
     */
    private void loadStoryDetails() {
        storiesRef.child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Story story = snapshot.getValue(Story.class);
                if (story != null) {
                    tvChapterListTitleHeader.setText("Chương của: " + story.getTitle()); // Cập nhật tiêu đề màn hình

                    tvStoryDetailTitle.setText(story.getTitle());
                    tvStoryDetailGenre.setText("Thể loại: " + (story.getCategory() != null ? story.getCategory() : "N/A"));
                    tvStoryDetailDescription.setText(story.getDescription());

                    // Tải ảnh bìa truyện
                    String imageUrl = story.getImageResource();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        int resId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
                        if (resId != 0) {
                            Glide.with(ListChapEdit.this).load(resId).into(ivStoryCover);
                        } else {
                            Glide.with(ListChapEdit.this).load(imageUrl)
                                    .placeholder(R.drawable.lgds) // Placeholder nếu là URL
                                    .error(R.drawable.lgds) // Ảnh lỗi
                                    .into(ivStoryCover);
                        }
                    } else {
                        ivStoryCover.setImageResource(R.drawable.lgds); // Ảnh mặc định
                    }

                } else {
                    Toast.makeText(ListChapEdit.this, "Không tìm thấy thông tin truyện.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListChapEdit.this, "Lỗi tải thông tin truyện: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi tải thông tin truyện: " + error.getMessage());
                finish();
            }
        });
    }

    /**
     * Tải danh sách các chương của truyện từ Firebase.
     */
    private void loadChapters() {
        progressBarChapters.setVisibility(View.VISIBLE);
        chaptersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chapterList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                        Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                        if (chapter != null) {
                            chapter.setId(chapterSnapshot.getKey()); // Đảm bảo set ID chương
                            chapterList.add(chapter);
                        } else {
                            // Nếu Chapter không ánh xạ trực tiếp (ví dụ: chỉ là Map<String, Boolean>)
                            // Bạn cần tạo Chapter object thủ công ở đây
                            String chapterId = chapterSnapshot.getKey();
                            // Bạn cần biết cấu trúc của chapterSnapshot để đọc đúng
                            // Ví dụ nếu nó chỉ là một boolean:
                            // Boolean isCompleted = chapterSnapshot.getValue(Boolean.class);
                            // Chapter simpleChapter = new Chapter(chapterId, "Chương " + chapterId, "");
                            // chapterList.add(simpleChapter);
                            Log.w(TAG, "Chapter object is null for key: " + chapterId + ". Check Firebase structure for chapters.");
                        }
                    }
                }
                chapterAdapter.notifyDataSetChanged();
                progressBarChapters.setVisibility(View.GONE);
                if (chapterList.isEmpty()) {
                    Toast.makeText(ListChapEdit.this, "Chưa có chương nào cho truyện này.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarChapters.setVisibility(View.GONE);
                Toast.makeText(ListChapEdit.this, "Lỗi tải chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi tải chương: " + error.getMessage());
            }
        });
    }

    /**
     * Xóa một chương khỏi Firebase.
     * @param chapterId ID của chương cần xóa.
     */
    private void deleteChapter(String chapterId) {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn xóa chương này?")
                .setCancelable(false)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    chaptersRef.child(chapterId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ListChapEdit.this, "Xóa chương thành công", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ListChapEdit.this, "Lỗi khi xóa chương: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Lỗi khi xóa chương: " + e.getMessage());
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_CHAPTER_REQUEST && resultCode == Activity.RESULT_OK) {
            loadChapters(); // Tải lại danh sách chương sau khi chỉnh sửa
            Toast.makeText(this, "Chương đã được cập nhật.", Toast.LENGTH_SHORT).show();
        }
    }
}
