package com.example.doan.truyen;

import static android.widget.Toast.makeText;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.example.doan.R;
import com.example.doan.adapter.ChapterAdapter;
import com.example.doan.model.Chapter;
import com.example.doan.model.Truyen;

import java.util.ArrayList;
import java.util.List;

public class DocTruyen extends AppCompatActivity {
    public class ChiTietTruyen extends AppCompatActivity {
        private TextView txtTenTruyen, txtMoTa;
        private RecyclerView recyclerViewChapters;
        private ChapterAdapter chapterAdapter;
        private List<Chapter> chapterList = new ArrayList<>();
        private DatabaseReference databaseReference;
        private String truyenId;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chi_tiet_truyen);

            txtTenTruyen = findViewById(R.id.txtTenTruyen);
            txtMoTa = findViewById(R.id.txtMoTa);
            recyclerViewChapters = findViewById(R.id.recyclerViewChapters);

            truyenId = getIntent().getStringExtra("TRUYEN_ID");

            recyclerViewChapters.setLayoutManager(new LinearLayoutManager(this));
            chapterAdapter = new ChapterAdapter(this, chapterList);
            recyclerViewChapters.setAdapter(chapterAdapter);

            loadTruyenInfo();
            loadChapters();
        }

        private void loadTruyenInfo() {
            databaseReference = FirebaseDatabase.getInstance().getReference("Truyen").child(truyenId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Truyen truyen = snapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        txtTenTruyen.setText(truyen.getTenTruyen());
                        txtMoTa.setText(truyen.getMoTa());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    makeText(ChiTietTruyen.this, "", Toast.LENGTH_SHORT).show();
                    makeText(ChiTietTruyen.this, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void loadChapters() {
            databaseReference = FirebaseDatabase.getInstance().getReference("Chapters").child(truyenId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chapterList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Chapter chapter = dataSnapshot.getValue(Chapter.class);
                        chapterList.add(chapter);
                    }
                    chapterAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    makeText(ChiTietTruyen.this, "Lỗi khi lấy danh sách chương!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
