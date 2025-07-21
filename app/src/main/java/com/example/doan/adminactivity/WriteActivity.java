package com.example.doan.adminactivity; // Đổi package thành com.example.doan.writer

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView; // Import ImageView
import android.widget.ProgressBar;
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; // Import SearchView
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.StoryAdapter2;
import com.example.doan.adminactivity.EditChapters;
import com.example.doan.homestory.AddChappter;
import com.example.doan.homestory.AddStory;
import com.example.doan.homestory.EditStory;
import com.example.doan.adminactivity.EditChapters;
import com.example.doan.model.Story;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StoryAdapter2 storyAdapter2;
    private List<Story> storyList; // Danh sách hiển thị (đã lọc)
    private List<Story> originalStoryList; // Danh sách gốc (tất cả truyện)
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAddStory;
    private ProgressBar progressBar;
    private ImageView backforget; // Nút quay lại
    private SearchView searchViewStories; // Thanh tìm kiếm
    private TextView tvTotalStoriesCount; // TextView hiển thị tổng số truyện

    private static final String TAG = "WriteActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write); // Sử dụng layout activity_write.xml

        Log.d(TAG, "WriteActivity onCreate called.");

        // Ánh xạ View
        recyclerView = findViewById(R.id.recyclerViewStories);
        fabAddStory = findViewById(R.id.fab_add_story);
        progressBar = findViewById(R.id.progressBar);
        backforget = findViewById(R.id.backforget);
        searchViewStories = findViewById(R.id.searchViewStories); // Ánh xạ SearchView
        tvTotalStoriesCount = findViewById(R.id.tvTotalStoriesCount); // Ánh xạ TextView tổng số truyện

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        storyList = new ArrayList<>();
        originalStoryList = new ArrayList<>(); // Khởi tạo danh sách gốc

        // Khởi tạo adapter
        storyAdapter2 = new StoryAdapter2(this, storyList, new StoryAdapter2.OnStoryActionListener() {
            @Override
            public void onDeleteStory(String storyId) {
                deleteStory(storyId);
            }

            @Override
            public void onEditStory(Story story) {
                Intent intent = new Intent(WriteActivity.this, EditStory.class);
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
            @Override
            public void onStoryClick(Story story) {
                Toast.makeText(WriteActivity.this, "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteActivity.this, AddChappter.class);
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
            @Override
            public void onEditChapterList(Story story) {
                Toast.makeText(WriteActivity.this, "Chỉnh sửa chương cho: " + story.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteActivity.this, ListChapEdit.class);
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
        });

        storyAdapter2.setWriteFragment(true);
        recyclerView.setAdapter(storyAdapter2);

        // Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Tải dữ liệu từ Firebase
        loadStories();

        // Sự kiện khi bấm nút thêm truyện
        fabAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(WriteActivity.this, AddStory.class);
            startActivity(intent);
        });

        // Sự kiện khi bấm nút quay lại
        backforget.setOnClickListener(view -> {
            finish();
        });

        // Thiết lập Listener cho SearchView
        setupSearchView();
    }

    private void loadStories() {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi đang tải dữ liệu

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalStoryList.clear(); // Xóa danh sách gốc trước khi tải mới
                for (DataSnapshot data : snapshot.getChildren()) {
                    Story story = data.getValue(Story.class);
                    if (story != null) {
                        story.setId(data.getKey()); // Gán ID từ Firebase
                        originalStoryList.add(story); // Thêm vào danh sách gốc
                    }
                }
                // Sau khi tải xong, lọc danh sách với query hiện tại (hoặc rỗng)
                filterStories(searchViewStories.getQuery().toString());
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi tải xong
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WriteActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi tải dữ liệu: " + error.getMessage());
            }
        });
    }

    private void setupSearchView() {
        searchViewStories.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterStories(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterStories(newText);
                return false;
            }
        });
    }

    private void filterStories(String query) {
        storyList.clear(); // Xóa danh sách hiển thị hiện tại

        if (query.isEmpty()) {
            storyList.addAll(originalStoryList); // Nếu query rỗng, hiển thị tất cả truyện
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Story story : originalStoryList) {
                if (story.getTitle() != null && story.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        story.getCategory() != null && story.getCategory().toLowerCase().contains(lowerCaseQuery)) {
                    storyList.add(story); // Thêm truyện khớp vào danh sách hiển thị
                }
            }
        }
        storyAdapter2.notifyDataSetChanged(); // Cập nhật RecyclerView
        updateTotalStoriesCount(); // Cập nhật TextView tổng số truyện
    }

    private void updateTotalStoriesCount() {
        if (tvTotalStoriesCount != null) {
            tvTotalStoriesCount.setText("Tổng số truyện: " + storyList.size());
        }
    }

    public void deleteStory(String storyId) {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn xóa truyện này?")
                .setCancelable(false)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    databaseReference.child(storyId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(WriteActivity.this, "Xóa truyện thành công", Toast.LENGTH_SHORT).show();
                                // Không cần loadStories() lại toàn bộ, Firebase Listener sẽ tự động cập nhật
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(WriteActivity.this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Lỗi khi xóa truyện: " + e.getMessage());
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
