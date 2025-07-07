package com.example.doan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.Chapter;
import com.example.doan.ui.ChapterDetailActivity;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private List<Chapter> chapterList;
    private Context context;

    public ChapterAdapter(Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.txtTenChapter.setText(chapter.getTitle());

        // Khi người dùng bấm vào một chương
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterDetailActivity.class);
            intent.putExtra("storyId", chapter.getStoryId());
            intent.putExtra("chapterId", chapter.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenChapter;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenChapter = itemView.findViewById(R.id.txtTenChapter);
        }
    }
}
