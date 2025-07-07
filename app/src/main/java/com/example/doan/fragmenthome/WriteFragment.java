package com.example.doan.fragmenthome;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.StoryAdapter;
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

public class WriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAddStory;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);

        // Ánh xạ View
        recyclerView = view.findViewById(R.id.recyclerViewStories);
        fabAddStory = view.findViewById(R.id.fab_add_story);
        progressBar = view.findViewById(R.id.progressBar);  // Thêm ProgressBar

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        storyList = new ArrayList<>();



        // Khởi tạo adapter
        storyAdapter = new StoryAdapter(getContext(), storyList, new StoryAdapter.OnStoryActionListener() {
            @Override
            public void onDeleteStory(String storyId) {
                deleteStory(storyId);
            }

            @Override
            public void onEditStory(Story story) {
                Intent intent = new Intent(getContext(), EditStory.class);
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
            @Override
            public void onStoryClick(Story story) {
                // Xử lý khi nhấn vào truyện
                Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

// Sau khi khởi tạo xong, mới gọi setWriteFragment(true)
        storyAdapter.setWriteFragment(true);
        recyclerView.setAdapter(storyAdapter);

        recyclerView.setAdapter(storyAdapter);

        // Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Tải dữ liệu từ Firebase
        loadStories();

        // Sự kiện khi bấm nút thêm truyện
        if (fabAddStory.getVisibility() == View.GONE || fabAddStory.getVisibility() == View.INVISIBLE) {
            fabAddStory.setVisibility(View.VISIBLE);
        }
        fabAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddStory.class);
            fabAddStory.setVisibility(View.VISIBLE);
            startActivity(intent);
        });

        // Sự kiện khi người dùng chọn một truyện
        storyAdapter.setOnItemClickListener(story -> {
            Intent intent = new Intent(getContext(), AddChappter.class);
            intent.putExtra("storyId", story.getId()); // Truyền ID của truyện sang AddChapterActivity
            startActivity(intent);
        });

        return view;
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
                storyAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi tải xong
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteStory(String storyId) {
        new AlertDialog.Builder(getContext())
                .setMessage("Bạn có chắc chắn muốn xóa truyện này?")
                .setCancelable(false)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    databaseReference.child(storyId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Xóa truyện thành công", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }



}
