package com.example.doan.Comment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.Comment.Comment;
import com.example.doan.Comment.CommentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChapterDetailActivity extends AppCompatActivity {
    private TextView tvChapterTitle, tvChapterContent;
    private ImageButton btnBack, btnPrevChapter, btnNextChapter;
    private ImageButton btnLike, btnComment, btnShare;
    private TextView tvLikeCount, tvCommentCount;
    private int likeCount = 0;
    private int commentCount = 0;
    private String storyId, chapterId;
    private DatabaseReference databaseReference;
    private List<String> chapterIds; // Danh sách các ID chương
    private int currentChapterIndex;
    private boolean isLiked = false;
    private List<Comment> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;
    
    // Biến để lưu thông tin user cho bình luận
    private String avatarUrlHolder = null;
    private String userNameHolder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);

        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterContent = findViewById(R.id.tvChapterContent);
        btnBack = findViewById(R.id.btnBack);
        btnPrevChapter = findViewById(R.id.btnPrevChapter);
        btnNextChapter = findViewById(R.id.btnNextChapter);

        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnShare = findViewById(R.id.btnShare);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        ImageView imgUserAvatar = findViewById(R.id.imgUserAvatar);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid());

            userRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String avatarUrl = snapshot.child("avatar").getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(imgUserAvatar);
                    }
                }
            });
        }

        // Xử lý sự kiện Like
        btnLike.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                likeCount++;
                btnLike.setImageResource(R.drawable.lovered);
            } else {
                likeCount = Math.max(0, likeCount - 1);
                btnLike.setImageResource(R.drawable.love);
            }
            tvLikeCount.setText(String.valueOf(likeCount));
            // Lưu lên Firebase
            if (storyId != null && chapterId != null) {
                DatabaseReference likeRef = FirebaseDatabase.getInstance()
                    .getReference("stories")
                    .child(storyId)
                    .child("chapters")
                    .child(chapterId)
                    .child("likeCount");
                likeRef.setValue(likeCount);
            }
        });
        // Xử lý sự kiện Comment - focus vào ô nhập bình luận
        btnComment.setOnClickListener(v -> {
            commentCount++;
            tvCommentCount.setText(String.valueOf(commentCount));
            // Focus vào ô nhập bình luận
            EditText edtComment = findViewById(R.id.edtComment);
            edtComment.requestFocus();
            // Hiện bàn phím
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(edtComment, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });
        // Xử lý sự kiện Share
        btnShare.setOnClickListener(v -> {
            String shareText = tvChapterTitle.getText().toString() + "\n\n" + tvChapterContent.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ chương truyện"));
        });

        btnPrevChapter.setOnClickListener(v -> loadChapter(-1)); // Chuyển đến chương trước
        btnNextChapter.setOnClickListener(v -> loadChapter(1));  // Chuyển đến chương sau

        btnBack.setOnClickListener(view -> finish());  // Quay lại màn trước

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");
        chapterId = intent.getStringExtra("chapterId");

        // Kiểm tra dữ liệu đầu vào
        if (storyId == null || chapterId == null) {
            Toast.makeText(this, "Lỗi: Thiếu dữ liệu chương!", Toast.LENGTH_SHORT).show();
            finish();  // Quay lại nếu thiếu dữ liệu
            return;
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
                    // Tìm vị trí của chương hiện tại
                    currentChapterIndex = chapterIds.indexOf(chapterId);
                    
                    loadChapter(0); // Tải chương hiện tại
                } else {
                    Toast.makeText(ChapterDetailActivity.this, "Không tìm thấy chương!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChapterDetailActivity.this, "Lỗi tải danh sách chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // THÊM CHỨC NĂNG BÌNH LUẬN MỚI (KHÔNG ĐỤNG CODE CŨ)
        RecyclerView recyclerViewComments = findViewById(R.id.recyclerViewComments);
        commentAdapter = new CommentAdapter(commentList);
        commentAdapter.setActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClick(Comment comment) {
                showReplyDialog(comment);
            }

            @Override
            public void onLikeClick(Comment comment) {
                if (comment.isLiked()) {
                    comment.setLiked(false);
                    comment.setLikeCount(comment.getLikeCount() - 1);
                } else {
                    comment.setLiked(true);
                    comment.setLikeCount(comment.getLikeCount() + 1);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLikeReplyClick(Comment reply) {
                if (reply.isLiked()) {
                    reply.setLiked(false);
                    reply.setLikeCount(reply.getLikeCount() - 1);
                } else {
                    reply.setLiked(true);
                    reply.setLikeCount(reply.getLikeCount() + 1);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onReplyToReplyClick(Comment reply) {
                showReplyToReplyDialog(reply);
            }
        });
        recyclerViewComments.setAdapter(commentAdapter);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        Button btnSendComment = findViewById(R.id.btnSendComment);
        EditText edtComment = findViewById(R.id.edtComment);
        
        // Lưu avatarUrl và userName để dùng cho bình luận
        
        if (currentUser != null) {
            // Thử lấy từ Firebase Realtime Database trước (như EditProfile.java)
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid());
            userRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String avatarUrl = snapshot.child("avatar").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        avatarUrlHolder = avatarUrl;
                        Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(imgUserAvatar);
                    }
                    if (username != null && !username.isEmpty()) {
                        userNameHolder = username;
                    }
                }
            }).addOnFailureListener(e -> {
                // Nếu không lấy được từ Realtime Database, thử Firestore (như HomePage.java)
                com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                db.collection("Users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String avatarUrl = documentSnapshot.getString("avatar");
                            String email = documentSnapshot.getString("email");
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                avatarUrlHolder = avatarUrl;
                                Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(imgUserAvatar);
                            }
                            if (email != null && !email.isEmpty()) {
                                userNameHolder = email.split("@")[0]; // Lấy phần trước @ làm tên
                            }
                        }
                    });
            });
        }
        
        // Xử lý gửi bình luận với chức năng mở rộng
        btnSendComment.setOnClickListener(v -> {
            String content = edtComment.getText().toString().trim();
            if (!content.isEmpty()) {
                // Sử dụng biến currentUser đã được khai báo trong scope của onCreate
                if (currentUser == null) {
                    Toast.makeText(this, "Vui lòng đăng nhập để bình luận!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userId = currentUser.getUid();
                String avatarUrl = avatarUrlHolder != null ? avatarUrlHolder : "";
                String userName = userNameHolder != null ? userNameHolder : "Người dùng";
                
                Comment comment = new Comment(avatarUrl, userName, content);
                comment.setUserId(userId);

                // Lưu lên Firebase theo cấu trúc danhgia/{storyId}/{chapterId}/{commentId}
                DatabaseReference commentsRef = FirebaseDatabase.getInstance()
                    .getReference("danhgia")
                    .child(storyId)
                    .child(chapterId);
                android.util.Log.d("FirebasePush", "Đang push tới đường dẫn: " + commentsRef.toString());
                String commentId = commentsRef.push().getKey();
                if (commentId != null) {
                    comment.setId(commentId);
                    Toast.makeText(this, "Đang gửi bình luận...", Toast.LENGTH_SHORT).show();
                    commentsRef.child(commentId).setValue(comment)
                        .addOnSuccessListener(aVoid -> {
                            edtComment.setText("");
                            Toast.makeText(ChapterDetailActivity.this, "✅ Đã gửi bình luận thành công!", Toast.LENGTH_SHORT).show();
                            android.util.Log.d("FirebasePush", "Comment saved successfully to: " + commentId);
                            loadComments();
                        })
                        .addOnFailureListener(e -> {
                            android.util.Log.e("FirebasePush", "Error saving comment: " + e.getMessage());
                            Toast.makeText(ChapterDetailActivity.this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                } else {
                    Toast.makeText(this, "❌ Lỗi: Không thể tạo ID comment!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        loadComments();
        
        edtComment.setFocusable(true);
        edtComment.setClickable(true);
        edtComment.setEnabled(true);

    }

    private void loadComments() {
        if (storyId == null || chapterId == null) {
            android.util.Log.e("LoadComments", "StoryId or ChapterId is null");
            return;
        }
        
        android.util.Log.d("LoadComments", "Loading comments from danhgia/" + storyId + "/" + chapterId);
        
        // Load comments từ danhgia/{storyId}/{chapterId}
        DatabaseReference commentsRef = FirebaseDatabase.getInstance()
            .getReference("danhgia")
            .child(storyId)
            .child(chapterId);

        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                android.util.Log.d("LoadComments", "Found " + snapshot.getChildrenCount() + " comments");
                
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        comment.setId(commentSnapshot.getKey()); // Đảm bảo ID được set
                        commentList.add(comment);
                        android.util.Log.d("LoadComments", "Loaded comment: " + comment.getContent());
                    }
                }
                commentAdapter.notifyDataSetChanged();
                tvCommentCount.setText(String.valueOf(commentList.size()));
                android.util.Log.d("LoadComments", "✅ Comments loaded successfully: " + commentList.size() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                android.util.Log.e("LoadComments", "Error loading comments: " + error.getMessage());
                Toast.makeText(ChapterDetailActivity.this, "❌ Lỗi tải bình luận: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChapter(int offset) {
        int newIndex = currentChapterIndex + offset;
        if (newIndex >= 0 && newIndex < chapterIds.size()) {
            currentChapterIndex = newIndex;
            chapterId = chapterIds.get(currentChapterIndex);
            databaseReference.child(chapterId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String title = snapshot.child("title").getValue(String.class);
                        String content = snapshot.child("content").getValue(String.class);
                        Long like = snapshot.child("likeCount").getValue(Long.class);
                        likeCount = (like != null) ? like.intValue() : 0;
                        tvLikeCount.setText(String.valueOf(likeCount));
                        if (title != null && content != null) {
                            tvChapterTitle.setText(title);
                            tvChapterContent.setText(content);
                        } else {
                            Toast.makeText(ChapterDetailActivity.this, "Dữ liệu chương không đầy đủ!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChapterDetailActivity.this, "Chương không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChapterDetailActivity.this, "Lỗi tải dữ liệu chương: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showReplyDialog(Comment parentComment) {
        // Tạo dialog để nhập reply
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Trả lời bình luận của " + parentComment.getUserName());

        // Tạo layout cho dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText edtReply = new EditText(this);
        edtReply.setHint("Nhập trả lời của bạn...");
        edtReply.setMinLines(3);
        edtReply.setMaxLines(5);
        layout.addView(edtReply);

        builder.setView(layout);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String replyContent = edtReply.getText().toString().trim();
            if (!replyContent.isEmpty()) {
                // Tạo reply comment
                String avatarUrl = avatarUrlHolder != null ? avatarUrlHolder : "";
                String userName = userNameHolder != null ? userNameHolder : "Người dùng";
                
                Comment reply = new Comment(avatarUrl, userName, replyContent, parentComment.getId());
                
                // Thêm reply vào comment gốc
                parentComment.addReply(reply);
                
                // Cập nhật adapter để hiển thị reply
                commentAdapter.notifyDataSetChanged();
                
                Toast.makeText(this, "Đã gửi trả lời!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung trả lời!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Focus vào EditText và hiện bàn phím
        edtReply.requestFocus();
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtReply, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showReplyToReplyDialog(Comment reply) {
        // Tạo dialog để nhập reply cho reply
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Trả lời " + reply.getUserName());

        // Tạo layout cho dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText edtReply = new EditText(this);
        edtReply.setHint("Nhập trả lời của bạn...");
        edtReply.setMinLines(3);
        edtReply.setMaxLines(5);
        layout.addView(edtReply);

        builder.setView(layout);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String replyContent = edtReply.getText().toString().trim();
            if (!replyContent.isEmpty()) {
                // Tạo reply cho reply (nested reply)
                String avatarUrl = avatarUrlHolder != null ? avatarUrlHolder : "";
                String userName = userNameHolder != null ? userNameHolder : "Người dùng";
                
                Comment nestedReply = new Comment(avatarUrl, userName, replyContent, reply.getId());
                
                // Thêm reply vào reply gốc
                reply.addReply(nestedReply);
                
                // Cập nhật adapter để hiển thị nested reply
                commentAdapter.notifyDataSetChanged();
                
                Toast.makeText(this, "Đã gửi trả lời!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung trả lời!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Focus vào EditText và hiện bàn phím
        edtReply.requestFocus();
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtReply, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // Xóa hàm loadComments() và các hàm liên quan đến reply qua database nếu có
}
