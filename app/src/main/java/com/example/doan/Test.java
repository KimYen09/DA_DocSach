package com.example.doan;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.doan.adapter.ViewAdapter;

public class Test extends AppCompatActivity {

    private ViewPager2 mView;
    private BottomNavigationView mbottomNavigationView;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mView = findViewById(R.id.mView);
        mbottomNavigationView = findViewById(R.id.mn);

        // Khởi tạo Adapter cho ViewPager2
        ViewAdapter adapter = new ViewAdapter(this);
        mView.setAdapter(adapter);

        // Tạo callback để có thể unregister sau này
        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
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
        };

        // Đăng ký callback
        mView.registerOnPageChangeCallback(pageChangeCallback);

        mbottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home) {
                mView.setCurrentItem(0);
            }
            else if(item.getItemId() == R.id.nav_search) {
                mView.setCurrentItem(1);
            }
            else if(item.getItemId() == R.id.nav_library) {
                mView.setCurrentItem(2);
            }
            else if(item.getItemId() == R.id.nav_write) {
                mView.setCurrentItem(3);
            }
            else if(item.getItemId() == R.id.nav_profile) {
                mView.setCurrentItem(4);
            }
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup để tránh memory leak
        if (mView != null && pageChangeCallback != null) {
            mView.unregisterOnPageChangeCallback(pageChangeCallback);
        }
        if (mbottomNavigationView != null) {
            mbottomNavigationView.setOnItemSelectedListener(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleanup khi activity bị pause
        if (mView != null && pageChangeCallback != null) {
            mView.unregisterOnPageChangeCallback(pageChangeCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đăng ký lại callback khi activity resume
        if (mView != null && pageChangeCallback != null) {
            mView.registerOnPageChangeCallback(pageChangeCallback);
        }
    }
}