package com.example.doan.fragmenthome;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.BooksAdapter;
import com.example.doan.model.Book;
import com.example.doan.model.Books;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BooksAdapter bookAdapter;
    private List<Books> bookList;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo danh sách sách mẫu
        bookList = new ArrayList<>();
        bookList.add(new Books("Book Title 1", "Author 1", "https://example.com/book1.jpg", "Description of Book 1"));
        bookList.add(new Books("Book Title 2", "Author 2", "https://example.com/book2.jpg", "Description of Book 2"));

        // Thiết lập Adapter cho RecyclerView
        bookAdapter = new BooksAdapter(bookList, book -> {
            // Khi người dùng click vào một cuốn sách, chuyển đến BookDetailFragment
            BookDetailFragment bookDetailFragment = BookDetailFragment.newInstance(book);
            getFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment, bookDetailFragment)
                    .addToBackStack(null)  // Thêm vào back stack để người dùng có thể quay lại
                    .commit();
        });
        recyclerView.setAdapter(bookAdapter);

        return rootView;
    }
}

