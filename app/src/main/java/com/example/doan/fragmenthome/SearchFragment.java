package com.example.doan.fragmenthome;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.example.doan.R;
import com.example.doan.adapter.BookAdapter;
import com.example.doan.adapter.StoryAdapter3;
import com.example.doan.model.Book;
import com.example.doan.model.Story;
import com.example.doan.ui.ChapterListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private StoryAdapter3 storyAdapter3;
    private List<Story> storyList;
    private DatabaseReference databaseReference;

    ProgressBar progressBar;

    private List<Story> originalList = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBarSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStories(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchStories(newText);
                return false;
            }
        });




        storyList = new ArrayList<>();
        storyAdapter3 = new StoryAdapter3(getContext(), storyList, new StoryAdapter3.OnStoryActionListener() {
            @Override
            public void onStoryClick(Story story) {
                // DÒNG NÀY XỬ LÝ KHI CLICK VÀO TRUYỆN!
                Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ChapterListActivity.class);
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }


        });


        recyclerView.setAdapter(storyAdapter3);

        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        fetchStories();
        setupCategoryFilters(view);

        return view;
    }

//    private void searchStories(String query) {
//        progressBar.setVisibility(View.VISIBLE);
//        fetchStories(); // Cập nhật danh sách từ Firebase trước khi tìm kiếm
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Story> searchResults = new ArrayList<>();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Story story = data.getValue(Story.class);
//                    if (story != null && (story.getTitle().toLowerCase().contains(query.toLowerCase())
//                            || story.getCategory().toLowerCase().contains(query.toLowerCase()))) {
//                        story.setId(data.getKey());
//                        searchResults.add(story);
//                    }
//                }
//
//                if (searchResults.isEmpty()) {
//                    Toast.makeText(getContext(), "Không tìm thấy truyện nào.", Toast.LENGTH_SHORT).show();
//                }
//
//                storyAdapter.updateList(searchResults);
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getContext(), "Lỗi tìm kiếm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//



    private void searchStories(String query) {
        if (query.isEmpty()) {
            storyAdapter3.updateList(originalList); // Nếu không nhập gì, hiển thị danh sách gốc
            return;
        }

        List<Story> searchResults = new ArrayList<>();
        for (Story story : originalList) { // Lọc từ danh sách gốc
            if (story.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    story.getCategory().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(story);
            }
        }
        storyAdapter3.updateList(searchResults);
    }

//    private void setupSearchView() {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filterStories(query); // Dùng filterStories() thay vì gọi lại Firebase
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.isEmpty()) {
//                    storyAdapter.updateList(originalList); // Trả lại danh sách gốc
//                } else {
//                    filterStories(newText);
//                }
//                return false;
//            }
//        });
//    }



    private void fetchStories() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                originalList.clear(); // Luôn cập nhật originalList

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    if (story != null) {
                        story.setId(dataSnapshot.getKey());
                        storyList.add(story);
                        originalList.add(story);
                    }
                }

                storyAdapter3.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

                if (isAdded() && getContext() != null) {
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }






    private void filterStories(String query) {
        List<Story> filteredList = new ArrayList<>();
        for (Story story : originalList) { // Lọc từ danh sách gốc
            if (story.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    story.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(story);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy truyện", Toast.LENGTH_SHORT).show();
        }

        storyAdapter3.updateList(filteredList);
    }




    private void setupCategoryFilters(View view) {
        view.findViewById(R.id.tvRomance).setOnClickListener(v -> filterByCategory("Lãng mạn"));
        view.findViewById(R.id.tvSciFi).setOnClickListener(v -> filterByCategory("Viễn tưởng"));
        view.findViewById(R.id.tvHorror).setOnClickListener(v -> filterByCategory("Kinh dị"));
    }

    private void filterByCategory(String category) {
        List<Story> filteredList = new ArrayList<>();

        // Lọc từ originalList thay vì storyList
        for (Story story : originalList) {
            if (story.getCategory().equals(category)) {
                filteredList.add(story);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Không có truyện trong danh mục này", Toast.LENGTH_SHORT).show();
        }

        storyAdapter3.updateList(filteredList);
    }


}
