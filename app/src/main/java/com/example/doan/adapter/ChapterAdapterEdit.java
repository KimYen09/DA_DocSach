package com.example.doan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.Chapter;

import java.util.List;

public class ChapterAdapterEdit extends RecyclerView.Adapter<ChapterAdapterEdit.ChapterViewHolder> {
    private List<Chapter> chapterList;
    private Context context;
    private OnChapterEditActionListener listener; // Sử dụng interface mới

    // Interface để xử lý các hành động trên chương
    public interface OnChapterEditActionListener {
        void onChapterClick(Chapter chapter); // Khi click vào toàn bộ item chương
        void onDeleteChapter(String chapterId); // Khi click vào nút xóa
        void onEditChapter(Chapter chapter); // Khi click vào nút chỉnh sửa
    }

    // Constructor mới nhận OnChapterActionListener
    public ChapterAdapterEdit(Context context, List<Chapter> chapterList, OnChapterEditActionListener listener) {
        this.context = context;
        this.chapterList = chapterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo inflate đúng layout item_chapter (có các nút edit/delete)
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter_edit, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        if (chapter != null) {
            holder.txtTenChapter.setText(chapter.getTitle() != null ? chapter.getTitle() : "Chương không tên");

            // OnClickListener cho toàn bộ item chương
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChapterClick(chapter); // Gọi callback onChapterClick
                }
            });

            // OnClickListener cho nút chỉnh sửa chương
            if (holder.btnEditChapter != null) { // Kiểm tra nút không null
                holder.btnEditChapter.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditChapter(chapter); // Gọi callback onEditChapter
                    }
                });
            } else {
                Log.e("ChapterAdapter", "btnEditChapter is null. Check item_chapter.xml for @id/btnEditChapter");
            }

            // OnClickListener cho nút xóa chương
            if (holder.btnDeleteChapter != null) { // Kiểm tra nút không null
                holder.btnDeleteChapter.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteChapter(chapter.getId()); // Gọi callback onDeleteChapter
                    }
                });
            } else {
                Log.e("ChapterAdapter", "btnDeleteChapter is null. Check item_chapter.xml for @id/btnDeleteChapter");
            }
        }
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenChapter;
        ImageView btnEditChapter; // Nút chỉnh sửa chương
        ImageView btnDeleteChapter; // Nút xóa chương


        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenChapter = itemView.findViewById(R.id.txtTenChapter);
            btnEditChapter = itemView.findViewById(R.id.btnEditChapter); // Ánh xạ nút chỉnh sửa
            btnDeleteChapter = itemView.findViewById(R.id.btnDeleteChapter); // Ánh xạ nút xóa
        }
    }
}

