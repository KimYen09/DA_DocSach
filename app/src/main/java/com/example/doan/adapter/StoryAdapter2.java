package com.example.doan.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.homestory.AddChappter;
import com.example.doan.model.Story;
import com.example.doan.ui.ChapterListActivity;

import java.util.List;

public class StoryAdapter2 extends RecyclerView.Adapter<StoryAdapter2.StoryViewHolder> {
    private Context context;
    private List<Story> stories;
    private OnItemClickListener listener;

    private boolean isWriteFragment = false;

    private OnStoryActionListener actionListener;

    public interface OnStoryActionListener {
        void onDeleteStory(String storyId);
        void onEditStory(Story story);
        void onStoryClick(Story story);
    }



    public StoryAdapter2(Context context, List<Story> stories, OnStoryActionListener actionListener) {
        this.context = context;
        this.stories = stories;
        this.actionListener = actionListener;
    }


    @Override
    public int getItemCount() {
        return stories.size();
    }




    public void setWriteFragment(boolean isWriteFragment) {
        this.isWriteFragment = isWriteFragment;
        notifyItemRangeChanged(0, stories.size());
    }


    public interface OnItemClickListener {
        void onItemClick(Story story);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, category;
        ImageView imageView, btnEdit, btnDelete;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txtStoryTitle2);
            category = itemView.findViewById(R.id.txtStoryCategory2);
            imageView = itemView.findViewById(R.id.imgStory2);
            btnEdit = itemView.findViewById(R.id.btnEditStory2);
            btnDelete = itemView.findViewById(R.id.btnDeleteStory2);
        }
    }


    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story2, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = stories.get(position);

        String originalTitle = story.getTitle();
        String displayTitle = originalTitle;

        Log.d("StoryAdapterDebug", "Original Title: " + originalTitle); // <-- Thêm dòng này

//        if (originalTitle != null && originalTitle.length() > 10) { // Giới hạn 40 ký tự
//            displayTitle = originalTitle.substring(0, 7) + "...";
//        }
        holder.titleTextView.setText(displayTitle);
        holder.category.setText(story.getCategory());

        String imageResource = story.getImageResource();
        Log.d("StoryAdapter", "imageResource: " + imageResource);

        if (imageResource != null && !imageResource.isEmpty()) {
            if (imageResource.startsWith("http")) {
                Glide.with(holder.itemView.getContext())
                        .load(imageResource)
                        .into(holder.imageView);
            } else {
                int imageResId = getImageResourceId(imageResource);
                holder.imageView.setImageResource(imageResId);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.lgsach2);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(story);
            }
        });


        if (isWriteFragment) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener!= null){
                listener.onItemClick(story);
            }
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditStory(story);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteStory(story.getId());
            }
        });



    }


    private int getImageResourceId(String imageName) {
        String validImageName = imageName.replaceAll("[^a-zA-Z0-9_]", "");

        int resId = context.getResources().getIdentifier(validImageName, "drawable", context.getPackageName());

        if (resId != 0) {
            return resId;
        } else {
            Log.e("StoryAdapter", "Không tìm thấy tài nguyên drawable với tên: " + validImageName);
            return R.drawable.lgsach2;
        }
    }


    public void updateList(List<Story> newList) {
        this.stories.clear();
        this.stories.addAll(newList);
        notifyDataSetChanged();
    }





}