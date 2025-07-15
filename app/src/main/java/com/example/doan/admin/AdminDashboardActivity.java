package com.example.doan.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.doan.R;
import com.example.doan.account.Login; // Giả sử bạn có Activity Login
import com.example.doan.admin.fragment.AllStoriesAdminFragment;
import com.example.doan.admin.fragment.PendingStoriesAdminFragment;

public class AdminDashboardActivity extends AppCompatActivity {

    private Toolbar adminToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminToolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(adminToolbar); // Thiết lập Toolbar làm ActionBar

        // Mặc định hiển thị Fragment danh sách tất cả truyện khi khởi động
        if (savedInstanceState == null) {
            replaceFragment(new AllStoriesAdminFragment(), "all_stories");
            adminToolbar.setTitle("Tất cả Truyện"); // Đặt tiêu đề ban đầu
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String title = "";

        if (id == R.id.action_all_stories) {
            fragment = new AllStoriesAdminFragment();
            title = "Tất cả Truyện";
        } else if (id == R.id.action_pending_stories) {
            fragment = new PendingStoriesAdminFragment();
            title = "Truyện Chờ Duyệt";
//        } else if (id == R.id.action_manage_premium_packages) {
//            fragment = new ManagePremiumPackagesAdminFragment();
//            title = "Quản lý Gói Premium";
//        } else if (id == R.id.action_story_statistics) {
//            fragment = new StoryStatisticsAdminFragment();
//            title = "Thống kê Truyện";
        } else if (id == R.id.action_logout) {
            // Xử lý đăng xuất admin (ví dụ: FirebaseAuth.getInstance().signOut();)
            Toast.makeText(this, "Đăng xuất Admin", Toast.LENGTH_SHORT).show();
            // Điều hướng về màn hình đăng nhập
            Intent intent = new Intent(AdminDashboardActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if (fragment != null) {
            replaceFragment(fragment, title);
            adminToolbar.setTitle(title); // Cập nhật tiêu đề Toolbar
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragment_container, fragment, tag);
        fragmentTransaction.commit();
    }
}