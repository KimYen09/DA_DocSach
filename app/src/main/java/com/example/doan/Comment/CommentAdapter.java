package com.example.doan.Comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.Comment.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private OnCommentActionListener actionListener;

    public interface OnCommentActionListener {
        void onReplyClick(Comment comment);
        void onLikeClick(Comment comment);
        void onLikeReplyClick(Comment reply);
        void onReplyToReplyClick(Comment reply);
    }

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setActionListener(OnCommentActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvUserName.setText(comment.getUserName());
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getTime());
        holder.tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
        
        // Hiển thị icon like dựa trên trạng thái
        if (comment.isLiked()) {
            holder.btnLikeComment.setImageResource(R.drawable.lovered);
        } else {
            holder.btnLikeComment.setImageResource(R.drawable.love);
        }
        
        // Load avatar
        Glide.with(holder.itemView.getContext())
            .load(comment.getAvatarUrl())
            .placeholder(R.drawable.avatar)
            .into(holder.imgAvatar);
        
        // Xử lý nút like
        holder.btnLikeComment.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onLikeClick(comment);
            } else {
                // Fallback nếu không có listener
                if (comment.isLiked()) {
                    comment.setLiked(false);
                    comment.setLikeCount(comment.getLikeCount() - 1);
                    holder.btnLikeComment.setImageResource(R.drawable.love);
                } else {
                    comment.setLiked(true);
                    comment.setLikeCount(comment.getLikeCount() + 1);
                    holder.btnLikeComment.setImageResource(R.drawable.lovered);
                }
                holder.tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            }
        });
        
        // Xử lý nút trả lời
        holder.tvReplyComment.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onReplyClick(comment);
            } else {
                Toast.makeText(holder.itemView.getContext(), 
                    "Trả lời bình luận của " + comment.getUserName(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Hiển thị replies
        holder.layoutReplies.removeAllViews();
        if (!comment.getReplies().isEmpty()) {
            holder.layoutReplies.setVisibility(View.VISIBLE);
            
            for (Comment reply : comment.getReplies()) {
                View replyView = createReplyView(holder.layoutReplies, reply);
                holder.layoutReplies.addView(replyView);
            }
        } else {
            holder.layoutReplies.setVisibility(View.GONE);
        }
    }

    private View createReplyView(ViewGroup parent, Comment reply) {
        View replyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        
        ImageView imgReplyAvatar = replyView.findViewById(R.id.imgReplyAvatar);
        TextView tvReplyUserName = replyView.findViewById(R.id.tvReplyUserName);
        TextView tvReplyContent = replyView.findViewById(R.id.tvReplyContent);
        TextView tvReplyTime = replyView.findViewById(R.id.tvReplyTime);
        TextView tvReplyLikeCount = replyView.findViewById(R.id.tvReplyLikeCount);
        ImageButton btnLikeReply = replyView.findViewById(R.id.btnLikeReply);
        
        tvReplyUserName.setText(reply.getUserName());
        tvReplyContent.setText(reply.getContent());
        tvReplyTime.setText(reply.getTime());
        tvReplyLikeCount.setText(String.valueOf(reply.getLikeCount()));
        
        // Hiển thị icon like dựa trên trạng thái
        if (reply.isLiked()) {
            btnLikeReply.setImageResource(R.drawable.lovered);
        } else {
            btnLikeReply.setImageResource(R.drawable.love);
        }
        
        // Load avatar
        Glide.with(parent.getContext())
            .load(reply.getAvatarUrl())
            .placeholder(R.drawable.avatar)
            .into(imgReplyAvatar);
        
        // Xử lý nút like cho reply
        btnLikeReply.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onLikeReplyClick(reply);
            } else {
                if (reply.isLiked()) {
                    reply.setLiked(false);
                    reply.setLikeCount(reply.getLikeCount() - 1);
                    btnLikeReply.setImageResource(R.drawable.love);
                } else {
                    reply.setLiked(true);
                    reply.setLikeCount(reply.getLikeCount() + 1);
                    btnLikeReply.setImageResource(R.drawable.lovered);
                }
                tvReplyLikeCount.setText(String.valueOf(reply.getLikeCount()));
            }
        });
        
        // Xử lý nút trả lời cho reply
        TextView tvReplyToReply = replyView.findViewById(R.id.tvReplyToReply);
        tvReplyToReply.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onReplyToReplyClick(reply);
            } else {
                Toast.makeText(parent.getContext(), 
                    "Trả lời reply của " + reply.getUserName(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        return replyView;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUserName;
        TextView tvContent;
        TextView tvTime;
        TextView tvLikeCount;
        TextView tvReplyComment;
        ImageButton btnLikeComment;
        LinearLayout layoutReplies;
        
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.txtUsername);
            tvContent = itemView.findViewById(R.id.txtCommentContent);
            tvTime = itemView.findViewById(R.id.txtTimestamp);
            tvLikeCount = itemView.findViewById(R.id.txtLikeCount);
            tvReplyComment = itemView.findViewById(R.id.txtReply);
            btnLikeComment = itemView.findViewById(R.id.btnLikeComment);
            layoutReplies = itemView.findViewById(R.id.layoutReplies);
        }
    }
} 