package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.Story;
import com.example.doan.utils.PremiumAccessHelper;
import java.util.List;

/**
 * Adapter cho danh sách truyện có hỗ trợ premium
 */
public class StoryPremiumAdapter extends RecyclerView.Adapter<StoryPremiumAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyList;
    private OnStoryClickListener listener;

    public interface OnStoryClickListener {
        void onStoryClick(Story story);
    }

    public StoryPremiumAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    public void setOnStoryClickListener(OnStoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story_premium, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);

        holder.titleTextView.setText(story.getTitle());
        holder.descriptionTextView.setText(story.getDescription());
        holder.categoryTextView.setText(story.getCategory());

        // Hiển thị ảnh bìa
        if (story.getImageResource() != null && !story.getImageResource().isEmpty()) {
            Glide.with(context)
                .load(story.getImageResource())
                .placeholder(R.drawable.lgsach)
                .error(R.drawable.lgsach)
                .into(holder.coverImageView);
        }

        // Hiển thị badge premium nếu cần
        if (PremiumAccessHelper.shouldShowPremiumBadge(story)) {
            holder.premiumBadge.setVisibility(View.VISIBLE);
        } else {
            holder.premiumBadge.setVisibility(View.GONE);
        }

        // Xử lý click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // Kiểm tra quyền truy cập trước khi mở truyện
                PremiumAccessHelper.checkStoryAccess(context, story, new PremiumAccessHelper.AccessCheckCallback() {
                    @Override
                    public void onAccessGranted() {
                        listener.onStoryClick(story);
                    }

                    @Override
                    public void onAccessDenied() {
                        // Dialog đã được hiển thị trong PremiumAccessHelper
                        // Không cần làm gì thêm
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList != null ? storyList.size() : 0;
    }

    public void updateData(List<Story> newStoryList) {
        this.storyList = newStoryList;
        notifyDataSetChanged();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView categoryTextView;
        View premiumBadge;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.story_cover_image);
            titleTextView = itemView.findViewById(R.id.story_title);
            descriptionTextView = itemView.findViewById(R.id.story_description);
            categoryTextView = itemView.findViewById(R.id.story_category);
            premiumBadge = itemView.findViewById(R.id.premium_badge);
        }
    }
}
