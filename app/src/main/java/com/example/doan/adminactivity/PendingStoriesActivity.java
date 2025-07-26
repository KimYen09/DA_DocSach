package com.example.doan.adminactivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.StoryAdminAdapter;
import com.example.doan.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingStoriesActivity extends AppCompatActivity implements StoryAdminAdapter.OnStoryActionListener {

    private static final String TAG = "PendingStoriesActivity";

    // UI Components
    private RecyclerView recyclerView;
    private StoryAdminAdapter adapter;
    private EditText searchEditText;
    private ProgressBar progressBar;
    private TextView totalStoriesTextView;
    private LinearLayout emptyStateLayout;
    private Toolbar toolbar;

    // Data
    private List<Story> allStoryList;
    private List<Story> filteredStoryList;

    // Firebase
    private DatabaseReference pendingStoriesRef;
    private DatabaseReference approvedStoriesRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_stories);

        Log.d(TAG, "=== PendingStoriesActivity onCreate started ===");

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check authentication
        if (currentUser == null) {
            Log.e(TAG, "User not logged in!");
            showAuthenticationError();
            return;
        }

        Log.d(TAG, "User logged in: " + currentUser.getUid());

        // Initialize Firebase references
        pendingStoriesRef = FirebaseDatabase.getInstance().getReference("pending_stories");
        approvedStoriesRef = FirebaseDatabase.getInstance().getReference("stories");

        // Initialize UI
        initializeViews();

        // Proceed directly to load data (no admin check needed)
        proceedWithDataLoading();
    }

    private void proceedWithDataLoading() {
        // Kiểm tra nếu có view nào bị null
        if (recyclerView == null || totalStoriesTextView == null || emptyStateLayout == null) {
            Log.e(TAG, "Critical views are null! Layout might have issues.");
            // Thử force hiển thị dữ liệu test
            addTestStories();
            showTestDataDirectly();
            return;
        }

        setupRecyclerView();
        setupSearch();

        // Load data - luôn hiển thị test data trước
        addTestStories();
        filterStories("");
        updateUI();

        // Sau đó mới load Firebase data
        loadPendingStoriesFromFirebase();
    }

    private void initializeViews() {
        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        // Initialize other views with null checks
        recyclerView = findViewById(R.id.recyclerViewPendingStories);
        searchEditText = findViewById(R.id.editTextSearchPendingStory);
        progressBar = findViewById(R.id.progressBarPendingStories);
        totalStoriesTextView = findViewById(R.id.totalPendingStoriesTextView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        // Check if all views are properly initialized
        if (recyclerView == null) {
            Log.e(TAG, "recyclerViewPendingStories not found in layout");
        }
        if (searchEditText == null) {
            Log.e(TAG, "editTextSearchPendingStory not found in layout");
        }
        if (progressBar == null) {
            Log.e(TAG, "progressBarPendingStories not found in layout");
        }
        if (totalStoriesTextView == null) {
            Log.e(TAG, "totalPendingStoriesTextView not found in layout");
        }
        if (emptyStateLayout == null) {
            Log.e(TAG, "emptyStateLayout not found in layout");
        }
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            allStoryList = new ArrayList<>();
            filteredStoryList = new ArrayList<>();
            adapter = new StoryAdminAdapter(this, filteredStoryList, this);
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView setup completed");
        } else {
            Log.e(TAG, "RecyclerView is null, cannot setup");
        }
    }

    private void setupSearch() {
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterStories(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            Log.d(TAG, "Search setup completed");
        } else {
            Log.e(TAG, "SearchEditText is null, cannot setup search");
        }
    }

    private void loadPendingStories() {
        Log.d(TAG, "Starting to load pending stories...");
        showLoading(true);

        // Thêm dữ liệu test trước để kiểm tra UI
        addTestDataIfEmpty();

        pendingStoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Firebase data changed, snapshot exists: " + snapshot.exists());
                Log.d(TAG, "Children count: " + snapshot.getChildrenCount());

                allStoryList.clear();

                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    try {
                        Story story = storySnapshot.getValue(Story.class);
                        if (story != null) {
                            story.setId(storySnapshot.getKey());
                            allStoryList.add(story);
                            Log.d(TAG, "Added story: " + story.getTitle());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing story: " + e.getMessage());
                    }
                }

                // Nếu không có dữ liệu từ Firebase, thêm dữ liệu test
                if (allStoryList.isEmpty()) {
                    Log.d(TAG, "No data from Firebase, adding test data");
                    addTestStories();
                }

                filterStories(searchEditText != null ? searchEditText.getText().toString().trim() : "");
                updateUI();
                showLoading(false);

                Log.d(TAG, "Loaded " + allStoryList.size() + " pending stories");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Log.e(TAG, "Failed to load pending stories: " + error.getMessage());

                // Thêm dữ liệu test khi Firebase lỗi
                addTestStories();
                updateUI();

                Toast.makeText(PendingStoriesActivity.this,
                    "Lỗi khi tải dữ liệu: " + error.getMessage() + ". Hiển thị dữ liệu test.",
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterStories(String query) {
        filteredStoryList.clear();

        if (query.isEmpty()) {
            filteredStoryList.addAll(allStoryList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Story story : allStoryList) {
                if ((story.getTitle() != null && story.getTitle().toLowerCase().contains(lowerCaseQuery)) ||
                    (story.getCategory() != null && story.getCategory().toLowerCase().contains(lowerCaseQuery)) ||
                    (story.getDescription() != null && story.getDescription().toLowerCase().contains(lowerCaseQuery))) {
                    filteredStoryList.add(story);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void addTestDataIfEmpty() {
        // Method này sẽ được gọi để thêm dữ liệu test nếu Firebase trống
        Log.d(TAG, "Checking if test data needed...");
    }

    private void addTestStories() {
        Log.d(TAG, "Adding test stories for debugging...");
        allStoryList.clear();

        // Tạo một vài truyện test để kiểm tra UI
        Story story1 = new Story();
        story1.setId("test_1");
        story1.setTitle("Truyện Test 1 - Hành trình phiêu lưu");
        story1.setDescription("Đây là mô tả truyện test số 1. Một câu chuyện về hành trình phiêu lưu đầy thú vị.");
        story1.setCategory("Phiêu lưu");
        story1.setUserId("user_test_1");
        story1.setCreationDate("2024-01-15");
        story1.setType("Truyện dài");

        Story story2 = new Story();
        story2.setId("test_2");
        story2.setTitle("Truyện Test 2 - Tình yêu học đường");
        story2.setDescription("Câu chuyện tình yêu ngọt ngào giữa hai học sinh trung học.");
        story2.setCategory("Tình cảm");
        story2.setUserId("user_test_2");
        story2.setCreationDate("2024-01-16");
        story2.setType("Truyện ngắn");

        Story story3 = new Story();
        story3.setId("test_3");
        story3.setTitle("Truyện Test 3 - Kinh dị đêm khuya");
        story3.setDescription("Một câu chuyện kinh dị rùng rợn xảy ra vào đêm khuya.");
        story3.setCategory("Kinh dị");
        story3.setUserId("user_test_3");
        story3.setCreationDate("2024-01-17");
        story3.setType("Truyện dài");

        allStoryList.add(story1);
        allStoryList.add(story2);
        allStoryList.add(story3);

        Log.d(TAG, "Added " + allStoryList.size() + " test stories");
    }

    private void updateUI() {
        Log.d(TAG, "Updating UI with " + filteredStoryList.size() + " stories");

        if (totalStoriesTextView != null) {
            totalStoriesTextView.setText("Tổng: " + filteredStoryList.size() + " truyện");
        }

        if (filteredStoryList.isEmpty()) {
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
                Log.d(TAG, "Showing empty state");
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG, "Showing RecyclerView with data");
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter notified of data change");
            }
        }
    }

    private void showLoading(boolean show) {
        Log.d(TAG, "Setting loading state: " + show);
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showAuthenticationError() {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi xác thực")
                .setMessage("Bạn cần đăng nhập để truy cập tính năng này.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showTestDataDirectly() {
        Log.d(TAG, "Showing test data directly due to layout issues");

        // Hiển thị Toast để user biết có vấn đề
        Toast.makeText(this, "Layout có vấn đề, hiển thị dữ liệu test", Toast.LENGTH_LONG).show();

        // Tạo dialog hiển thị dữ liệu test
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Truyện chờ duyệt (Test Data)");

        StringBuilder testData = new StringBuilder();
        for (Story story : allStoryList) {
            testData.append("📖 ").append(story.getTitle()).append("\n");
            testData.append("👤 Tác giả: ").append(story.getUserId()).append("\n");
            testData.append("📂 Thể loại: ").append(story.getCategory()).append("\n");
            testData.append("📅 Ngày: ").append(story.getCreationDate()).append("\n\n");
        }

        builder.setMessage(testData.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void loadPendingStoriesFromFirebase() {
        Log.d(TAG, "Loading stories from Firebase...");

        pendingStoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Firebase data received, children count: " + snapshot.getChildrenCount());

                List<Story> firebaseStories = new ArrayList<>();

                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    try {
                        Story story = storySnapshot.getValue(Story.class);
                        if (story != null) {
                            story.setId(storySnapshot.getKey());
                            firebaseStories.add(story);
                            Log.d(TAG, "Firebase story: " + story.getTitle());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Firebase story: " + e.getMessage());
                    }
                }

                // Nếu có dữ liệu từ Firebase, thay thế test data
                if (!firebaseStories.isEmpty()) {
                    Log.d(TAG, "Using Firebase data instead of test data");
                    allStoryList.clear();
                    allStoryList.addAll(firebaseStories);
                    filterStories("");
                    updateUI();

                    Toast.makeText(PendingStoriesActivity.this,
                        "Đã tải " + firebaseStories.size() + " truyện từ Firebase",
                        Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "No Firebase data, keeping test data");
                    Toast.makeText(PendingStoriesActivity.this,
                        "Không có dữ liệu Firebase, hiển thị dữ liệu test",
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());

                String errorMessage = "Lỗi Firebase: " + error.getMessage() + ". Sử dụng dữ liệu test.";
                Toast.makeText(PendingStoriesActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onApproveStory(Story story) {
        new AlertDialog.Builder(this)
                .setTitle("Duyệt truyện")
                .setMessage("Bạn có chắc chắn muốn duyệt truyện \"" + story.getTitle() + "\"?")
                .setPositiveButton("Duyệt", (dialog, which) -> approveStory(story))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onRejectStory(Story story) {
        new AlertDialog.Builder(this)
                .setTitle("Từ chối truyện")
                .setMessage("Bạn có chắc chắn muốn từ chối truyện \"" + story.getTitle() + "\"?")
                .setPositiveButton("Từ chối", (dialog, which) -> rejectStory(story))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onViewStoryDetails(Story story) {
        // Create a detailed view dialog
        showStoryDetailsDialog(story);
    }

    private void approveStory(Story story) {
        if (story.getId() == null) {
            Toast.makeText(this, "Lỗi: Không thể xác định ID truyện", Toast.LENGTH_SHORT).show();
            return;
        }

        // Move story from pending to approved
        Map<String, Object> updates = new HashMap<>();
        updates.put("stories/" + story.getId(), story);
        updates.put("pending_stories/" + story.getId(), null);

        FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đã duyệt truyện \"" + story.getTitle() + "\"",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Story approved: " + story.getTitle());
                    } else {
                        Toast.makeText(this, "Lỗi khi duyệt truyện: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to approve story", task.getException());
                    }
                });
    }

    private void rejectStory(Story story) {
        if (story.getId() == null) {
            Toast.makeText(this, "Lỗi: Không thể xác định ID truyện", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove story from pending (rejection)
        pendingStoriesRef.child(story.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đã từ chối truyện \"" + story.getTitle() + "\"",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Story rejected: " + story.getTitle());
                    } else {
                        Toast.makeText(this, "Lỗi khi từ chối truyện: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to reject story", task.getException());
                    }
                });
    }

    private void showStoryDetailsDialog(Story story) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(story.getTitle() != null ? story.getTitle() : "Chi tiết truyện");

        String details = "Thể loại: " + (story.getCategory() != null ? story.getCategory() : "Không rõ") + "\n\n" +
                        "Tác giả: " + (story.getUserId() != null ? story.getUserId() : "Ẩn danh") + "\n\n" +
                        "Mô tả: " + (story.getDescription() != null ? story.getDescription() : "Không có mô tả") + "\n\n" +
                        "Ngày tạo: " + (story.getCreationDate() != null ? story.getCreationDate() : "Không rõ") + "\n\n" +
                        "Loại: " + (story.getType() != null ? story.getType() : "Không rõ");

        builder.setMessage(details);
        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up listeners if needed
    }
}
