package com.example.doan;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.doan.adapter.ViewAdapter;

public class Test extends AppCompatActivity {

    private ViewPager2 mView;
    private BottomNavigationView mbottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mView = findViewById(R.id.mView);
        mbottomNavigationView = findViewById(R.id.mn);

        // Khởi tạo Adapter cho ViewPager2
        ViewAdapter adapter = new ViewAdapter(this);
        mView.setAdapter(adapter);


        // Xử lý thay đổi trang
        mView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: mbottomNavigationView.setSelectedItemId(R.id.nav_home); break;
                    case 1: mbottomNavigationView.setSelectedItemId(R.id.nav_search); break;
                    case 2: mbottomNavigationView.setSelectedItemId(R.id.nav_library); break;
                    case 3: mbottomNavigationView.setSelectedItemId(R.id.nav_write); break;
                    case 4: mbottomNavigationView.setSelectedItemId(R.id.nav_profile); break;
                }
            }
        });


        mbottomNavigationView.setOnItemSelectedListener(item -> {
                if(item.getItemId() == R.id.nav_home) {
                    mView.setCurrentItem(0);
                }
                else if(item.getItemId() == R.id.nav_search) {
                    mView.setCurrentItem(1);
                }
                if(item.getItemId() == R.id.nav_library) {
                    mView.setCurrentItem(2);
                }
                if(item.getItemId() == R.id.nav_write) {
                mView.setCurrentItem(3);
                }
                if(item.getItemId() == R.id.nav_profile) {
                    mView.setCurrentItem(4);
                }
                return true;
        });


    }
}