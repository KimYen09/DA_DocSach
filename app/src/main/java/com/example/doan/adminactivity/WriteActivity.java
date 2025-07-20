package com.example.doan.adminactivity; // Đổi package thành com.example.doan.writer

import android.app.AlertDialog; // Sử dụng AlertDialog từ android.app cho Activity
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log; // Thêm import cho Log
import android.view.View; // Import View cho findViewById
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // Kế thừa AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.StoryAdapter2;
import com.example.doan.homestory.AddChappter;
import com.example.doan.homestory.AddStory;
import com.example.doan.homestory.EditStory;
import com.example.doan.model.Story;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WriteActivity extends AppCompatActivity { // Đổi tên lớp và kế thừa AppCompatActivity
    private RecyclerView recyclerView;
    private StoryAdapter2 storyAdapter2;
    private List<Story> storyList;
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAddStory;
    private ProgressBar progressBar;
    ImageView backforget;

    private static final String TAG = "WriteActivity"; // Thêm TAG cho Log

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { // Đổi tên phương thức từ onCreateView sang onCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write); // Sử dụng layout XML của fragment

        Log.d(TAG, "WriteActivity onCreate called.");

        // Ánh xạ View
        // Không cần view.findViewById nữa vì đây là Activity
        recyclerView = findViewById(R.id.recyclerViewStories);
        fabAddStory = findViewById(R.id.fab_add_story);
        progressBar = findViewById(R.id.progressBar);
        backforget = findViewById(R.id.backforget);

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Sử dụng 'this' cho Context
        storyList = new ArrayList<>();

        // Khởi tạo adapter
        storyAdapter2 = new StoryAdapter2(this, storyList, new StoryAdapter2.OnStoryActionListener() { // Sử dụng 'this' cho Context
            @Override
            public void onDeleteStory(String storyId) {
                deleteStory(storyId);
            }

            @Override
            public void onEditStory(Story story) {
                Intent intent = new Intent(WriteActivity.this, EditStory.class); // Sử dụng WriteActivity.this cho Context
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
            @Override
            public void onStoryClick(Story story) {
                // Xử lý khi nhấn vào truyện
                Toast.makeText(WriteActivity.this, "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show(); // Sử dụng WriteActivity.this cho Context
                Intent intent = new Intent(WriteActivity.this, AddChappter.class); // Sử dụng WriteActivity.this cho Context
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
        });

        // Sau khi khởi tạo xong, mới gọi setWriteFragment(true)
        storyAdapter2.setWriteFragment(true);
        recyclerView.setAdapter(storyAdapter2);

        // Dòng này bị trùng, có thể xóa
        // recyclerView.setAdapter(storyAdapter2);

        // Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Tải dữ liệu từ Firebase
        loadStories();

        // Sự kiện khi bấm nút thêm truyện
        if (fabAddStory.getVisibility() == View.GONE || fabAddStory.getVisibility() == View.INVISIBLE) {
            fabAddStory.setVisibility(View.VISIBLE);
        }
        fabAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(WriteActivity.this, AddStory.class); // Sử dụng WriteActivity.this cho Context
            fabAddStory.setVisibility(View.VISIBLE);
            startActivity(intent);
        });



//         Sự kiện khi người dùng chọn một truyện (đã được xử lý trong OnStoryActionListener.onStoryClick)
//         Dòng này bị trùng logic với onStoryClick trong OnStoryActionListener, có thể xóa
         storyAdapter2.setOnItemClickListener(story -> {
             Intent intent = new Intent(WriteActivity.this, AddChappter.class);
             intent.putExtra("storyId", story.getId());
             startActivity(intent);
         });
         backforget.setOnClickListener(view -> {
             finish();
         });
    }

    private void loadStories() {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi đang tải dữ liệu

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Story story = data.getValue(Story.class);
                    if (story != null) {
                        story.setId(data.getKey()); // Gán ID từ Firebase
                        storyList.add(story);
                    }
                }

                // Cập nhật adapter
                storyAdapter2.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi tải xong
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WriteActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show(); // Sử dụng WriteActivity.this cho Context
                Log.e(TAG, "Lỗi tải dữ liệu: " + error.getMessage()); // Log lỗi
            }
        });
    }

    public void deleteStory(String storyId) {
        new AlertDialog.Builder(this) // Sử dụng 'this' cho Context của Activity
                .setMessage("Bạn có chắc chắn muốn xóa truyện này?")
                .setCancelable(false)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    databaseReference.child(storyId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(WriteActivity.this, "Xóa truyện thành công", Toast.LENGTH_SHORT).show(); // Sử dụng WriteActivity.this cho Context
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(WriteActivity.this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Sử dụng WriteActivity.this cho Context
                                Log.e(TAG, "Lỗi khi xóa truyện: " + e.getMessage()); // Log lỗi
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
