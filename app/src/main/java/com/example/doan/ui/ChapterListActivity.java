//package com.example.doan.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.doan.R;
//import com.example.doan.adapter.ChapterAdapter;
//import com.example.doan.fragmenthome.HomeFragment;
//import com.example.doan.model.Chapter;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChapterListActivity extends AppCompatActivity {
//    private RecyclerView recyclerView;
//    private ProgressBar progressBar;
//    private DatabaseReference databaseReference;
//    private List<Chapter> chapterList;
//
//    private ChapterAdapter chapterAdapter;
//    private String storyId;
//    ImageButton btnBack;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chapter_list);
//
//        recyclerView = findViewById(R.id.recyclerViewChapters);
//        progressBar = findViewById(R.id.progressBar);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        btnBack = findViewById(R.id.btnBack);
//
//        btnBack.setOnClickListener(view -> {
//            finish(); // Quay lại màn trước đó
//        });
//
//        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(divider);
//
//        chapterList = new ArrayList<>();
//        chapterAdapter = new ChapterAdapter(this, chapterList);
//        recyclerView.setAdapter(chapterAdapter);
//
//        // Nhận storyId từ Intent
//        storyId = getIntent().getStringExtra("storyId");
//        if (storyId == null) {
//            Toast.makeText(this, "Không tìm thấy ID truyện!", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories").child(storyId).child("chapters");
//
//        loadChapters();
//    }
//
//
//    private void loadChapters() {
//        progressBar.setVisibility(View.VISIBLE);
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                chapterList.clear();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    String id = data.getKey();
//                    String title = data.child("title").getValue(String.class);
//                    String content = data.child("content").getValue(String.class);
//
//                    if (id != null && title != null && content != null) {
//                        chapterList.add(new Chapter(id, title, content, storyId));
//                    }
//
//                }
//
//                chapterAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChapterListActivity.this, "Lỗi tải chương!", Toast.LENGTH_SHORT).show();
//                Log.e("FirebaseError", "Lỗi Firebase: " + error.getMessage());
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
//
//}
package com.example.doan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Không cần import Glide nữa nếu chỉ dùng drawable
// import com.bumptech.glide.Glide;

import com.example.doan.R;
import com.example.doan.adapter.ChapterAdapter;
import com.example.doan.model.Chapter;
import com.example.doan.model.Story;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChapterListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private List<Chapter> chapterList;

    private ChapterAdapter chapterAdapter;
    private String storyId;
    ImageButton btnBack;

    private ImageView ivStoryCover;
    private TextView tvStoryDetailTitle;
    private TextView tvStoryDetailGenre;
    private TextView tvStoryDetailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        recyclerView = findViewById(R.id.recyclerViewChapters);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        ivStoryCover = findViewById(R.id.ivStoryCover);
        tvStoryDetailTitle = findViewById(R.id.tvStoryDetailTitle);
        tvStoryDetailGenre = findViewById(R.id.tvStoryDetailGenre);
        tvStoryDetailDescription = findViewById(R.id.tvStoryDetailDescription);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(view -> {
            finish();
        });

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(this, chapterList);
        recyclerView.setAdapter(chapterAdapter);

        storyId = getIntent().getStringExtra("storyId");
        if (storyId == null) {
            Toast.makeText(this, "Không tìm thấy ID truyện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        loadStoryDetailsAndChapters();
    }

    private void loadStoryDetailsAndChapters() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Story story = snapshot.getValue(Story.class);
                    if (story != null) {
                        tvStoryDetailTitle.setText(story.getTitle());
                        String genre = story.getCategory();
                        if (genre != null && !genre.isEmpty()) {
                            tvStoryDetailGenre.setText("Thể loại: " + genre);
                        } else {
                            tvStoryDetailGenre.setText("Thể loại: Không rõ");
                        }
                        tvStoryDetailDescription.setText(story.getDescription());

                        // --- THAY ĐỔI QUAN TRỌNG Ở ĐÂY ---
                        // Lấy tên file drawable từ Firebase (ví dụ: "story_cover_1")
                        String drawableName = story.getImageResource(); // Bây giờ imageUrl chứa tên file drawable

                        if (drawableName != null && !drawableName.isEmpty()) {
                            // Chuyển tên file drawable thành ID tài nguyên
                            int resourceId = getResources().getIdentifier(
                                    drawableName, "drawable", getPackageName());

                            if (resourceId != 0) { // Nếu tìm thấy ID tài nguyên
                                ivStoryCover.setImageResource(resourceId);
                            } else {
                                // Nếu không tìm thấy drawable, đặt ảnh mặc định
                                ivStoryCover.setImageResource(R.drawable.lgsach);
                                Log.e("ChapterListActivity", "Không tìm thấy drawable: " + drawableName);
                            }
                        } else {
                            // Nếu tên drawable rỗng hoặc null, đặt ảnh mặc định
                            ivStoryCover.setImageResource(R.drawable.lgsach);
                        }
                        // --- KẾT THÚC THAY ĐỔI ---
                    }
                } else {
                    Toast.makeText(ChapterListActivity.this, "Không tìm thấy thông tin truyện!", Toast.LENGTH_SHORT).show();
                }

                loadChapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChapterListActivity.this, "Lỗi tải thông tin truyện!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Lỗi Firebase khi tải thông tin truyện: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadChapters() {
        FirebaseDatabase.getInstance().getReference("stories").child(storyId).child("chapters")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chapterList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String id = data.getKey();
                            String title = data.child("title").getValue(String.class);
                            String content = data.child("content").getValue(String.class);

                            if (id != null && title != null && content != null) {
                                chapterList.add(new Chapter(id, title, content, storyId));
                            }
                        }
                        chapterAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChapterListActivity.this, "Lỗi tải chương!", Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseError", "Lỗi Firebase khi tải chương: " + error.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}