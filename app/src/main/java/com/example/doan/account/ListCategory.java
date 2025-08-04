package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.adapter.CategoryAdapter;

import java.util.Arrays;
import java.util.List;

public class ListCategory extends AppCompatActivity {

    ImageButton listback;
    Button btnCotinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);
//        EdgeToEdge.enable(this);

        btnCotinue = findViewById(R.id.btnContinue);
        GridView gridView = findViewById(R.id.gridViewCategories);


        List<String> categories = Arrays.asList("Bí Ẩn","Chicklit", "Cổ Điển","Hài Hước",
                "Hành Động","Kinh Dị",
                "Lãng Mạn", "Ma Cà Rồng", "Ngẫu Nhiên",
                "Phiêu Lưu","Phi Tiểu Thuyết",
                "Tâm Linh", "Thơ Ca",
                "Truyện Ngắn","Khoa Học Viễn Tưởng");

        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        gridView.setAdapter(adapter);

        listback = findViewById(R.id.listback);
        listback.setOnClickListener(view -> {
            Intent itlistback = new Intent(ListCategory.this, PassRegister.class);
            startActivity(itlistback);
        });

        btnCotinue.setOnClickListener(view -> {
            Intent itcontinue = new Intent(ListCategory.this, Premium.class);
            startActivity(itcontinue);
        });



    }
}