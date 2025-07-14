package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private FirebaseUser currentUser;

    public interface OnCommentActionListener {
        void onLikeComment(Comment comment, boolean isLiked);
        void onReplyComment(Comment comment);
    }

    private OnCommentActionListener actionListener;

    public CommentAdapter(Context context, List<Comment> commentList, OnCommentActionListener actionListener) {
        this.context = context;
        this.commentList = commentList;
        this.actionListener = actionListener;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        
        // Hiển thị thông tin comment
        holder.txtUsername.setText(comment.getUsername());
        holder.txtCommentContent.setText(comment.getContent());
        holder.txtLikeCount.setText(String.valueOf(comment.getLikeCount()));
        
        // Hiển thị thời gian
        String timeAgo = getTimeAgo(comment.getTimestamp());
        holder.txtTimestamp.setText(timeAgo);
        
        // Hiển thị avatar
        if (comment.getUserAvatar() != null && !comment.getUserAvatar().isEmpty()) {
            Glide.with(context).load(comment.getUserAvatar()).into(holder.imgUserAvatar);
        }
        
        // Cập nhật trạng thái like
        updateLikeButton(holder, comment);
        
        // Xử lý sự kiện like
        holder.btnLikeComment.setOnClickListener(v -> {
            if (currentUser != null) {
                boolean isLiked = comment.toggleLike(currentUser.getUid());
                holder.txtLikeCount.setText(String.valueOf(comment.getLikeCount()));
                updateLikeButton(holder, comment);
                
                if (actionListener != null) {
                    actionListener.onLikeComment(comment, isLiked);
                }
            } else {
                Toast.makeText(context, "Vui lòng đăng nhập để thích bình luận", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Xử lý sự kiện reply
        holder.txtReply.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onReplyComment(comment);
            }
        });
    }

    private void updateLikeButton(CommentViewHolder holder, Comment comment) {
        if (currentUser != null && comment.isLikedByUser(currentUser.getUid())) {
            holder.btnLikeComment.setImageResource(R.drawable.ic_heart);
        } else {
            holder.btnLikeComment.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private String getTimeAgo(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - timestamp;
        
        long seconds = timeDiff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " ngày trước";
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else if (minutes > 0) {
            return minutes + " phút trước";
        } else {
            return "Vừa xong";
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateList(List<Comment> newList) {
        this.commentList.clear();
        this.commentList.addAll(newList);
        notifyDataSetChanged();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserAvatar;
        TextView txtUsername, txtTimestamp, txtCommentContent, txtLikeCount, txtReply;
        ImageButton btnLikeComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
            txtCommentContent = itemView.findViewById(R.id.txtCommentContent);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
            txtReply = itemView.findViewById(R.id.txtReply);
            btnLikeComment = itemView.findViewById(R.id.btnLikeComment);
        }
    }
}

