package com.example.doan.Comment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.ChapterAdapter;
import com.example.doan.model.Chapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        recyclerView = findViewById(R.id.recyclerViewChapters);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> {
            finish(); // Quay lại màn trước đó
        });

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(this, chapterList);
        recyclerView.setAdapter(chapterAdapter);

        // Nhận storyId từ Intent
        storyId = getIntent().getStringExtra("storyId");
        if (storyId == null) {
            Toast.makeText(this, "Không tìm thấy ID truyện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("stories").child(storyId).child("chapters");

        loadChapters();
    }


    private void loadChapters() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chapterList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                        Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                        if (chapter != null) {
                            chapter.setId(chapterSnapshot.getKey()); // Gán ID từ Firebase
                            chapterList.add(chapter);
                        }
                    }
                }
                chapterAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChapterListActivity.this, "Lỗi tải chương!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Lỗi Firebase: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
