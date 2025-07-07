package com.example.doan.adapter;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.Books;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksViewHolder> {

    private List<Books> books;
    private OnBookClickListener listener;

    public BooksAdapter(List<Books> books, OnBookClickListener listener) {
        this.books = books;
        this.listener = listener;
    }

    @Override
    public BooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false);
        return new BooksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BooksViewHolder holder, int position) {
        Books book = books.get(position);

        if (book == null) {
            Log.e("BooksAdapter", "Sách tại vị trí " + position + " bị null!");
            return;
        }

        if (holder.bookTitles != null) {
            holder.bookTitles.setText(book.getTitle());
        } else {
            Log.e("BooksAdapter", "bookTitles bị null! Kiểm tra ID trong item_books.xml");
        }


        if (holder.bookAuthors != null) {
            holder.bookAuthors.setText(book.getAuthor());
        }

        if (holder.bookImage != null) {
            Picasso.get().load(book.getImageUrl()).into(holder.bookImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }


    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class BooksViewHolder extends RecyclerView.ViewHolder {
        public TextView bookTitles, bookAuthors;
        public ImageView bookImage;

        public BooksViewHolder(View view) {
            super(view);
            bookTitles = view.findViewById(R.id.bookTitles);
            bookAuthors = view.findViewById(R.id.bookAuthors);
            bookImage = view.findViewById(R.id.bookImage);
        }

    }

    public interface OnBookClickListener {
        void onBookClick(Books book);
    }

}
