package com.example.doan.admin.fragment; // Tạo package mới cho fragments admin

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.admin.adapters.StoryAdminAdapter; // Sẽ tạo sau
import com.example.doan.model.Story;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllStoriesAdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoryAdminAdapter adapter;
    private List<Story> allStoryList; // Danh sách đầy đủ
    private List<Story> filteredStoryList; // Danh sách đã lọc
    private EditText searchEditText;
    private ProgressBar progressBar;
    private TextView totalStoriesTextView;

    private DatabaseReference storiesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_all_stories, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAllStories);
        searchEditText = view.findViewById(R.id.editTextSearchStory);
        progressBar = view.findViewById(R.id.progressBarAllStories);
        totalStoriesTextView = view.findViewById(R.id.totalStoriesTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allStoryList = new ArrayList<>();
        filteredStoryList = new ArrayList<>();
        adapter = new StoryAdminAdapter(getContext(), filteredStoryList); // Tạo adapter mới cho admin
        recyclerView.setAdapter(adapter);

        storiesRef = FirebaseDatabase.getInstance().getReference("stories");

        loadAllStories();
        setupSearch();

        return view;
    }

    private void loadAllStories() {
        progressBar.setVisibility(View.VISIBLE);
        storiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allStoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    if (story != null) {
                        story.setId(dataSnapshot.getKey()); // Đảm bảo ID được set
                        // Bạn có thể thêm logic để lấy số chương/lượt đọc ở đây nếu cần,
                        // hoặc đảm bảo chúng là một phần của mô hình Story
                        allStoryList.add(story);
                    }
                }
                filterStories(searchEditText.getText().toString()); // Lọc lại danh sách sau khi tải
                totalStoriesTextView.setText("Tổng số truyện: " + allStoryList.size());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải truyện: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AdminFragment", "Lỗi tải truyện: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterStories(String query) {
        filteredStoryList.clear();
        if (query.isEmpty()) {
            filteredStoryList.addAll(allStoryList);
        } else {
            for (Story story : allStoryList) {
                if (story.getTitle() != null && story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredStoryList.add(story);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}