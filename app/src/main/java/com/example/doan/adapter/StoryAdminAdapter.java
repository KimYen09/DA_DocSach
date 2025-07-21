package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.Story;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StoryAdminAdapter extends RecyclerView.Adapter<StoryAdminAdapter.PendingStoryViewHolder> {

    private Context context;
    private List<Story> stories;
    private OnStoryActionListener actionListener;

    public interface OnStoryActionListener {
        void onApproveStory(Story story);
        void onRejectStory(Story story);
        void onViewStoryDetails(Story story);
    }

    public StoryAdminAdapter(Context context, List<Story> stories, OnStoryActionListener actionListener) {
        this.context = context;
        this.stories = stories;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public PendingStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_story, parent, false);
        return new PendingStoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingStoryViewHolder holder, int position) {
        Story story = stories.get(position);

        // Set story details
        holder.txtStoryTitle.setText(story.getTitle() != null ? story.getTitle() : "Không có tiêu đề");
        holder.txtStoryAuthor.setText("Tác giả: " + (story.getUserId() != null ? story.getUserId() : "Ẩn danh"));
        holder.txtStoryCategory.setText(story.getCategory() != null ? story.getCategory() : "Chưa phân loại");
        holder.txtStoryDescription.setText(story.getDescription() != null ? story.getDescription() : "Không có mô tả");
        holder.txtCreationDate.setText("Ngày tạo: " + (story.getCreationDate() != null ? story.getCreationDate() : "Không rõ"));

        // Load story image
        if (story.getImageResource() != null && !story.getImageResource().isEmpty()) {
            Glide.with(context)
                    .load(story.getImageResource())
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(holder.imgStory);
        } else {
            holder.imgStory.setImageResource(R.drawable.img);
        }

        // Set click listeners
        holder.btnApprove.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onApproveStory(story);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRejectStory(story);
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onViewStoryDetails(story);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void updateStories(List<Story> newStories) {
        this.stories.clear();
        this.stories.addAll(newStories);
        notifyDataSetChanged();
    }

    public void removeStory(int position) {
        if (position >= 0 && position < stories.size()) {
            stories.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class PendingStoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStory;
        TextView txtStoryTitle;
        TextView txtStoryAuthor;
        TextView txtStoryCategory;
        TextView txtStoryDescription;
        TextView txtCreationDate;
        Button btnApprove;
        Button btnReject;
        ImageView btnViewDetails;

        public PendingStoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imgStory = itemView.findViewById(R.id.imgStory);
            txtStoryTitle = itemView.findViewById(R.id.txtStoryTitle);
            txtStoryAuthor = itemView.findViewById(R.id.txtStoryAuthor);
            txtStoryCategory = itemView.findViewById(R.id.txtStoryCategory);
            txtStoryDescription = itemView.findViewById(R.id.txtStoryDescription);
            txtCreationDate = itemView.findViewById(R.id.txtCreationDate);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
