//package com.example.doan.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.doan.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChapterDetailActivity extends AppCompatActivity {
//    private TextView tvChapterTitle, tvChapterContent;
//    private ImageButton btnBack, btnPrevChapter, btnNextChapter;
//    private String storyId, chapterId;
//    private DatabaseReference databaseReference;
//    private List<String> chapterIds; // Danh sách các ID chương
//    private int currentChapterIndex;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chapter_detail);
//
//        tvChapterTitle = findViewById(R.id.tvChapterTitle);
//        tvChapterContent = findViewById(R.id.tvChapterContent);
//        btnBack = findViewById(R.id.btnBack);
//        btnPrevChapter = findViewById(R.id.btnPrevChapter);
//        btnNextChapter = findViewById(R.id.btnNextChapter);
//
//        btnPrevChapter.setOnClickListener(v -> loadChapter(-1)); // Chuyển đến chương trước
//        btnNextChapter.setOnClickListener(v -> loadChapter(1));  // Chuyển đến chương sau
//
//        btnBack.setOnClickListener(view -> finish());  // Quay lại màn trước
//
//        // Nhận dữ liệu từ Intent
//        Intent intent = getIntent();
//        storyId = intent.getStringExtra("storyId");
//        chapterId = intent.getStringExtra("chapterId");
//
//        // Kiểm tra dữ liệu đầu vào
//        if (storyId == null || chapterId == null) {
//            Toast.makeText(this, "Lỗi: Thiếu dữ liệu chương!", Toast.LENGTH_SHORT).show();
//            finish();  // Quay lại nếu thiếu dữ liệu
//            return;
//        }
//
//        // Lấy danh sách các chương từ Firebase
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories")
//                .child(storyId).child("chapters");
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    chapterIds = new ArrayList<>();
//                    for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
//                        chapterIds.add(chapterSnapshot.getKey());
//                    }
//                    // Tìm vị trí của chương hiện tại
//                    currentChapterIndex = chapterIds.indexOf(chapterId);
//                    loadChapter(0); // Tải chương hiện tại
//                } else {
//                    Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy chương!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChapterDetailActivity.this, "Lỗi tải danh sách chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void loadChapter(int offset) {
//        int newIndex = currentChapterIndex + offset;
//        if (newIndex >= 0 && newIndex < chapterIds.size()) {
//            currentChapterIndex = newIndex;
//            chapterId = chapterIds.get(currentChapterIndex);
//            databaseReference.child(chapterId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        String title = snapshot.child("title").getValue(String.class);
//                        String content = snapshot.child("content").getValue(String.class);
//                        if (title != null && content != null) {
//                            tvChapterTitle.setText(title);
//                            tvChapterContent.setText(content);
//                        } else {
//                            Toast.makeText(ChapterDetailActivity.this, "Dữ liệu chương không đầy đủ!", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(ChapterDetailActivity.this, "Chương không tồn tại!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(ChapterDetailActivity.this, "Lỗi tải dữ liệu chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
package com.example.doan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
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
import java.util.Objects;

public class ChapterDetailActivity extends AppCompatActivity {
    private TextView tvChapterTitle, tvChapterContent;
    private ImageButton btnBack, btnPrevChapter, btnNextChapter;
    private ScrollView scrollViewContent;
    private ProgressBar progressBar;

    // Các View từ layout
    private ImageButton btnLike, btnGoToBookmark, btnShare, btnBookmark;
    private TextView tvCommentCount;
    private ImageView imgUserAvatar;
    private EditText edtCommentInput;
    private Button btnSendComment;
    private RecyclerView recyclerViewComments;
    private LinearLayout actionLayout, commentInputLayout, navigationLayout;

    private String storyId, chapterId;
    private DatabaseReference databaseReference;
    private DatabaseReference userHistoryRef;
    private DatabaseReference userBookmarkRef;
    private List<String> chapterIds;
    private int currentChapterIndex;
    private String currentUserId;

    private int lastSavedScrollPosition = 0;
    private String bookmarkedChapterId;
    private int bookmarkedScrollPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập. Lịch sử đọc và Bookmark sẽ không được lưu.", Toast.LENGTH_LONG).show();
            currentUserId = null;
        }

        // Ánh xạ các View từ layout
        btnBack = findViewById(R.id.btnBack);
        btnBookmark = findViewById(R.id.btnBookmark); // Đảm bảo ánh xạ đúng ID
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        scrollViewContent = findViewById(R.id.scrollView);
        tvChapterContent = findViewById(R.id.tvChapterContent);

        btnLike = findViewById(R.id.btnLike);
        btnGoToBookmark = findViewById(R.id.btnGoToBookmark);
        btnShare = findViewById(R.id.btnShare);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        edtCommentInput = findViewById(R.id.edtCommentInput);
        btnSendComment = findViewById(R.id.btnSendComment);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        actionLayout = findViewById(R.id.actionLayout);
        commentInputLayout = findViewById(R.id.commentInputLayout);

        navigationLayout = findViewById(R.id.navigationLayout);
        btnPrevChapter = findViewById(R.id.btnPrevChapter);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        progressBar = findViewById(R.id.progressBar);

        // Đặt Listener cho các nút điều hướng
        btnPrevChapter.setOnClickListener(v -> loadChapter(-1));
        btnNextChapter.setOnClickListener(v -> loadChapter(1));
        btnBack.setOnClickListener(view -> finish());

        // Listener cho nút Bookmark
        btnBookmark.setOnClickListener(v -> toggleBookmark());
        // Listener cho nút GoToBookmark
        btnGoToBookmark.setOnClickListener(v -> goToBookmarkedPosition());


        // Listener cho thanh tiến trình đọc (ProgressBar)
        scrollViewContent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollViewContent.getScrollY();
                int maxScroll = scrollViewContent.getChildAt(0).getHeight() - scrollViewContent.getHeight();

                if (maxScroll <= 0) {
                    progressBar.setProgress(100);
                } else {
                    int progress = (int) (100.0 * scrollY / maxScroll);
                    progressBar.setProgress(progress);
                }
                lastSavedScrollPosition = scrollY;
            }
        });

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");
        chapterId = intent.getStringExtra("chapterId");

        if (storyId == null) {
            Toast.makeText(this, "Lỗi: Thiếu dữ liệu truyện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo userHistoryRef và userBookmarkRef sau khi có storyId
        if (currentUserId != null) {
            userHistoryRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserId).child("readingHistory").child(storyId);
            userBookmarkRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserId).child("bookmarks").child(storyId);
        } else {
            // Nếu không có người dùng đăng nhập, ẩn nút bookmark và go-to-bookmark
            btnBookmark.setVisibility(View.GONE);
            btnGoToBookmark.setVisibility(View.GONE);
        }

        // Lấy danh sách các chương từ Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("stories")
                .child(storyId).child("chapters");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chapterIds = new ArrayList<>();
                    for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                        chapterIds.add(chapterSnapshot.getKey());
                    }
                    // Bạn có thể sắp xếp chapterIds ở đây nếu cần

                    if (chapterId == null && userHistoryRef != null) {
                        loadReadingHistory();
                    } else {
                        currentChapterIndex = chapterIds.indexOf(chapterId);
                        if (currentChapterIndex == -1) {
                            currentChapterIndex = 0;
                            Toast.makeText(ChapterDetailActivity.this, "Chương không hợp lệ, mở chương đầu tiên.", Toast.LENGTH_SHORT).show();
                        }
                        loadChapter(0);
                    }
                } else {
                    Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy chương cho truyện này!", Toast.LENGTH_SHORT).show();
                    // Ẩn các phần liên quan đến đọc nếu không có chương
                    tvChapterContent.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    navigationLayout.setVisibility(View.GONE);
                    actionLayout.setVisibility(View.GONE);
                    commentInputLayout.setVisibility(View.GONE);
                    tvCommentCount.setVisibility(View.GONE);
                    recyclerViewComments.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChapterDetailActivity.this, "Lỗi tải danh sách chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // Ẩn các phần liên quan đến đọc nếu có lỗi
                tvChapterContent.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                navigationLayout.setVisibility(View.GONE);
                actionLayout.setVisibility(View.GONE);
                commentInputLayout.setVisibility(View.GONE);
                tvCommentCount.setVisibility(View.GONE);
                recyclerViewComments.setVisibility(View.GONE);
            }
        });

        // --- Các listener cho các nút khác (Like, Share, SendComment) ---
        btnLike.setOnClickListener(v -> Toast.makeText(ChapterDetailActivity.this, "Nút Like được bấm!", Toast.LENGTH_SHORT).show());
        btnShare.setOnClickListener(v -> Toast.makeText(ChapterDetailActivity.this, "Nút Share được bấm!", Toast.LENGTH_SHORT).show());
        btnSendComment.setOnClickListener(v -> {
            String commentText = edtCommentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                Toast.makeText(ChapterDetailActivity.this, "Bình luận: " + commentText, Toast.LENGTH_SHORT).show();
                edtCommentInput.setText("");
                // Thêm logic lưu bình luận vào Firebase tại đây
            } else {
                Toast.makeText(ChapterDetailActivity.this, "Vui lòng nhập bình luận!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChapter(int offset) {
        int newIndex = currentChapterIndex + offset;
        if (newIndex >= 0 && newIndex < chapterIds.size()) {
            saveReadingHistory();

            currentChapterIndex = newIndex;
            chapterId = chapterIds.get(currentChapterIndex);

            databaseReference.child(chapterId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String title = snapshot.child("title").getValue(String.class);
                        String content = snapshot.child("content").getValue(String.class);
                        if (title != null && content != null) {
                            tvChapterTitle.setText(title);
                            tvChapterContent.setText(content);
                            scrollViewContent.scrollTo(0, 0);
                            progressBar.setVisibility(View.VISIBLE);

                            loadBookmarkState(); // Load trạng thái bookmark để hiển thị icon đúng

                            if (offset == 0 && userHistoryRef != null) {
                                userHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot historySnapshot) {
                                        if (historySnapshot.exists()) {
                                            String savedChapterId = historySnapshot.child("lastReadChapterId").getValue(String.class);
                                            Long savedScrollPos = historySnapshot.child("lastReadScrollPosition").getValue(Long.class);

                                            if (savedChapterId != null && Objects.equals(savedChapterId, chapterId) && savedScrollPos != null) {
                                                tvChapterContent.post(() -> {
                                                    scrollViewContent.scrollTo(0, savedScrollPos.intValue());
                                                    Log.d("ChapterDetail", "Cuộn đến vị trí lịch sử đọc đã lưu: " + savedScrollPos);
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("ChapterDetail", "Lỗi tải lịch sử đọc chi tiết: " + error.getMessage());
                                    }
                                });
                            }

                        } else {
                            Toast.makeText(ChapterDetailActivity.this, "Dữ liệu chương không đầy đủ!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(ChapterDetailActivity.this, "Chương không tồn tại!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChapterDetailActivity.this, "Lỗi tải dữ liệu chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(this, "Không có chương nào khác.", Toast.LENGTH_SHORT).show();
        }
    }

    // Các phương thức toggleBookmark(), updateBookmarkIcon(), loadBookmarkState(), goToBookmarkedPosition(),
    // saveReadingHistory(), loadReadingHistory() giữ nguyên như bản cập nhật trước đó
    // vì chúng đã xử lý chính xác việc lưu và cuộn đến vị trí bookmark/lịch sử đọc.

    private void toggleBookmark() {
        if (currentUserId == null || chapterId == null || userBookmarkRef == null) {
            Toast.makeText(this, "Không thể đánh dấu trang. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Objects.equals(chapterId, bookmarkedChapterId)) {
            userBookmarkRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        bookmarkedChapterId = null;
                        bookmarkedScrollPosition = 0;
                        updateBookmarkIcon(false);
                        Toast.makeText(ChapterDetailActivity.this, "Đã gỡ đánh dấu trang.", Toast.LENGTH_SHORT).show();
                        Log.d("ChapterDetail", "Bookmark removed successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ChapterDetailActivity.this, "Lỗi khi gỡ đánh dấu trang: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ChapterDetail", "Error removing bookmark: " + e.getMessage());
                    });
        } else {
            Map<String, Object> bookmark = new HashMap<>();
            bookmark.put("bookmarkedChapterId", chapterId);
            bookmark.put("bookmarkedScrollPosition", lastSavedScrollPosition);
            bookmark.put("timestamp", System.currentTimeMillis());

            userBookmarkRef.setValue(bookmark)
                    .addOnSuccessListener(aVoid -> {
                        bookmarkedChapterId = chapterId;
                        bookmarkedScrollPosition = lastSavedScrollPosition;
                        updateBookmarkIcon(true);
                        Toast.makeText(ChapterDetailActivity.this, "Đã đánh dấu trang thành công!", Toast.LENGTH_SHORT).show();
                        Log.d("ChapterDetail", "Bookmark saved successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ChapterDetailActivity.this, "Lỗi khi đánh dấu trang: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ChapterDetail", "Error saving bookmark: " + e.getMessage());
                    });
        }
    }

    private void updateBookmarkIcon(boolean isBookmarked) {
        if (isBookmarked) {
            btnBookmark.setImageResource(R.drawable.ic_edit);
        } else {
            btnBookmark.setImageResource(R.drawable.outline_edit_24);
        }
    }

    private void loadBookmarkState() {
        if (currentUserId != null && chapterId != null && userBookmarkRef != null) {
            userBookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String savedBookmarkChapterId = snapshot.child("bookmarkedChapterId").getValue(String.class);
                        Long savedBookmarkScrollPos = snapshot.child("bookmarkedScrollPosition").getValue(Long.class);

                        if (savedBookmarkChapterId != null) {
                            bookmarkedChapterId = savedBookmarkChapterId;
                            bookmarkedScrollPosition = savedBookmarkScrollPos != null ? savedBookmarkScrollPos.intValue() : 0;

                            if (Objects.equals(chapterId, bookmarkedChapterId)) {
                                updateBookmarkIcon(true);
                            } else {
                                updateBookmarkIcon(false);
                            }
                        } else {
                            bookmarkedChapterId = null;
                            bookmarkedScrollPosition = 0;
                            updateBookmarkIcon(false);
                        }
                    } else {
                        bookmarkedChapterId = null;
                        bookmarkedScrollPosition = 0;
                        updateBookmarkIcon(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ChapterDetail", "Lỗi tải trạng thái bookmark: " + error.getMessage());
                    updateBookmarkIcon(false);
                }
            });
        } else {
            bookmarkedChapterId = null;
            bookmarkedScrollPosition = 0;
            updateBookmarkIcon(false);
        }
    }

    private void goToBookmarkedPosition() {
        if (currentUserId == null || userBookmarkRef == null) {
            Toast.makeText(this, "Không thể đi đến trang đã đánh dấu. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        userBookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String targetChapterId = snapshot.child("bookmarkedChapterId").getValue(String.class);
                    Long targetScrollPos = snapshot.child("bookmarkedScrollPosition").getValue(Long.class);

                    if (targetChapterId != null && chapterIds.contains(targetChapterId)) {
                        if (Objects.equals(targetChapterId, chapterId)) {
                            if (targetScrollPos != null) {
                                scrollViewContent.post(() -> {
                                    scrollViewContent.scrollTo(0, targetScrollPos.intValue());
                                    Toast.makeText(ChapterDetailActivity.this, "Đã cuộn đến vị trí đánh dấu.", Toast.LENGTH_SHORT).show();
                                    Log.d("ChapterDetail", "Cuộn đến vị trí đánh dấu trong chương hiện tại: " + targetScrollPos);
                                });
                            } else {
                                Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy vị trí cuộn đánh dấu.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChapterDetailActivity.this, "Chuyển đến chương đã đánh dấu...", Toast.LENGTH_SHORT).show();
                            currentChapterIndex = chapterIds.indexOf(targetChapterId);
                            chapterId = targetChapterId;
                            if (targetScrollPos != null) {
                                lastSavedScrollPosition = targetScrollPos.intValue();
                            } else {
                                lastSavedScrollPosition = 0;
                            }
                            loadChapter(0);
                        }
                    } else {
                        Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy trang đánh dấu hoặc chương không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChapterDetailActivity.this, "Bạn chưa đánh dấu trang nào cho truyện này.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChapterDetailActivity.this, "Lỗi khi tải bookmark để di chuyển: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChapterDetail", "Error fetching bookmark for navigation: " + error.getMessage());
            }
        });
    }

    private void saveReadingHistory() {
        if (currentUserId != null && chapterId != null && userHistoryRef != null) {
            Map<String, Object> history = new HashMap<>();
            history.put("lastReadChapterId", chapterId);
            history.put("lastReadScrollPosition", lastSavedScrollPosition);
            history.put("timestamp", System.currentTimeMillis());

            userHistoryRef.setValue(history)
                    .addOnSuccessListener(aVoid -> Log.d("ChapterDetail", "Lịch sử đọc đã được lưu thành công."))
                    .addOnFailureListener(e -> Log.e("ChapterDetail", "Lỗi khi lưu lịch sử đọc: " + e.getMessage()));
        } else {
            Log.d("ChapterDetail", "Không thể lưu lịch sử đọc: Người dùng chưa đăng nhập hoặc chapterId/userHistoryRef là null.");
        }
    }

    private void loadReadingHistory() {
        if (userHistoryRef != null) {
            userHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String savedChapterId = snapshot.child("lastReadChapterId").getValue(String.class);
                        Long savedScrollPos = snapshot.child("lastReadScrollPosition").getValue(Long.class);

                        if (savedChapterId != null) {
                            currentChapterIndex = chapterIds.indexOf(savedChapterId);
                            if (currentChapterIndex == -1) {
                                currentChapterIndex = 0;
                                Toast.makeText(ChapterDetailActivity.this, "Chương đã đọc không tồn tại, mở chương đầu tiên.", Toast.LENGTH_SHORT).show();
                            }
                            chapterId = chapterIds.get(currentChapterIndex);
                            loadChapter(0);

                            if (savedScrollPos != null) {
                                lastSavedScrollPosition = savedScrollPos.intValue();
                            } else {
                                lastSavedScrollPosition = 0;
                            }

                        } else {
                            currentChapterIndex = 0;
                            loadChapter(0);
                            Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy chương đã đọc gần đây, mở chương đầu tiên.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        currentChapterIndex = 0;
                        loadChapter(0);
                        Toast.makeText(ChapterDetailActivity.this, "Không có lịch sử đọc cho truyện này, mở chương đầu tiên.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ChapterDetail", "Lỗi khi tải lịch sử đọc: " + error.getMessage());
                    currentChapterIndex = 0;
                    loadChapter(0);
                }
            });
        } else {
            currentChapterIndex = 0;
            loadChapter(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveReadingHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveReadingHistory();
    }
}