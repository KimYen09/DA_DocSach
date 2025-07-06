package com.example.doan.fragmenthome;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.example.doan.model.Books;

public class BookDetailFragment extends Fragment {

    private static final String ARG_BOOK = "book";

    // Khởi tạo fragment với đối tượng Book
    public static BookDetailFragment newInstance(Books book) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book); // Đưa đối tượng Book vào Bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Lấy đối tượng Book từ Bundle
            Books book = (Books) getArguments().getSerializable(ARG_BOOK);
            // Xử lý đối tượng Book
        }
    }

}
