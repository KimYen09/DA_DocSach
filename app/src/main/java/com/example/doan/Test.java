//package com.example.doan;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.Toolbar;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewpager2.widget.ViewPager2;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.example.doan.adapter.ViewAdapter;
//
//public class Test extends AppCompatActivity {
//
//    private ViewPager2 mView;
//    private BottomNavigationView mbottomNavigationView;
//    private ViewPager2.OnPageChangeCallback pageChangeCallback;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        mView = findViewById(R.id.mView);
//        mbottomNavigationView = findViewById(R.id.mn);
//
//        // Khởi tạo Adapter cho ViewPager2
//        ViewAdapter adapter = new ViewAdapter(this);
//        mView.setAdapter(adapter);
//
//        // Tạo callback để có thể unregister sau này
//        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                switch (position) {
//                    case 0: mbottomNavigationView.setSelectedItemId(R.id.nav_home); break;
//                    case 1: mbottomNavigationView.setSelectedItemId(R.id.nav_search); break;
//                    case 2: mbottomNavigationView.setSelectedItemId(R.id.nav_library); break;
//                    case 3: mbottomNavigationView.setSelectedItemId(R.id.nav_write); break;
//                    case 4: mbottomNavigationView.setSelectedItemId(R.id.nav_profile); break;
//                }
//            }
//        };
//
//        // Đăng ký callback
//        mView.registerOnPageChangeCallback(pageChangeCallback);
//
//        mbottomNavigationView.setOnItemSelectedListener(item -> {
//            if(item.getItemId() == R.id.nav_home) {
//                mView.setCurrentItem(0);
//            }
//            else if(item.getItemId() == R.id.nav_search) {
//                mView.setCurrentItem(1);
//            }
//            else if(item.getItemId() == R.id.nav_library) {
//                mView.setCurrentItem(2);
//            }
//            else if(item.getItemId() == R.id.nav_write) {
//                mView.setCurrentItem(3);
//            }
//            else if(item.getItemId() == R.id.nav_profile) {
//                mView.setCurrentItem(4);
//            }
//            return true;
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Cleanup để tránh memory leak
//        if (mView != null && pageChangeCallback != null) {
//            mView.unregisterOnPageChangeCallback(pageChangeCallback);
//        }
//        if (mbottomNavigationView != null) {
//            mbottomNavigationView.setOnItemSelectedListener(null);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Cleanup khi activity bị pause
//        if (mView != null && pageChangeCallback != null) {
//            mView.unregisterOnPageChangeCallback(pageChangeCallback);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Đăng ký lại callback khi activity resume
//        if (mView != null && pageChangeCallback != null) {
//            mView.registerOnPageChangeCallback(pageChangeCallback);
//        }
//    }
//}
package com.example.doan;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // Import MenuItem
import android.view.View; // Import View
import android.widget.RelativeLayout;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.doan.adapter.ViewAdapter;
import com.example.doan.model.User; // Import User model
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Test extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private ViewPager2 mView;
    private BottomNavigationView mbottomNavigationView;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    private boolean isUserAuthorizedForWrite = false; // Biến cờ kiểm tra quyền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mView = findViewById(R.id.mView);
        mbottomNavigationView = findViewById(R.id.mn);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Khởi tạo Adapter cho ViewPager2
        ViewAdapter adapter = new ViewAdapter(this);
        mView.setAdapter(adapter);

        // Tạo callback để có thể unregister sau này
        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Đảm bảo BottomNavigationView cập nhật đúng mục được chọn
                mbottomNavigationView.setSelectedItemId(mbottomNavigationView.getMenu().getItem(position).getItemId());
            }
        };

        // Đăng ký callback
        mView.registerOnPageChangeCallback(pageChangeCallback);

        mbottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                mView.setCurrentItem(0);
            } else if (itemId == R.id.nav_search) {
                mView.setCurrentItem(1);
            } else if (itemId == R.id.nav_library) {
                mView.setCurrentItem(2);
            } else if (itemId == R.id.nav_write) {
                // Kiểm tra quyền trước khi cho phép chuyển sang trang Write
                if (isUserAuthorizedForWrite) {
                    mView.setCurrentItem(3);
                } else {
                    Toast.makeText(Test.this, "Bạn không có quyền truy cập trang này.", Toast.LENGTH_SHORT).show();
                    // Giữ lại ở trang hiện tại hoặc chuyển về trang Home
                    mbottomNavigationView.setSelectedItemId(mbottomNavigationView.getMenu().getItem(mView.getCurrentItem()).getItemId());
                    return false; // Không cho phép chọn mục này
                }
            } else if (itemId == R.id.nav_profile) {
                mView.setCurrentItem(4);
            }
            return true;
        });

        // Vô hiệu hóa vuốt ViewPager2 nếu người dùng không có quyền
        mView.setUserInputEnabled(isUserAuthorizedForWrite); // Sẽ được cập nhật sau khi tải quyền

        // Tải quyền người dùng và cập nhật UI
        checkUserRoleAndAuthorize();
    }

    /**
     * Checks the current user's role from Firebase and updates UI/permissions.
     */
    private void checkUserRoleAndAuthorize() {
        if (currentUser == null) {
            Log.d(TAG, "No user logged in. Disabling write access.");
            updateWriteAccessUI(false);
            return;
        }

        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String role = user.getRole();
                        // Kiểm tra nếu role là "admin" hoặc "author"
                        if ("admin".equalsIgnoreCase(role) || "author".equalsIgnoreCase(role)) {
                            Log.d(TAG, "User is authorized for write: " + role);
                            isUserAuthorizedForWrite = true;
                        } else {
                            Log.d(TAG, "User is not authorized for write: " + role);
                            isUserAuthorizedForWrite = false;
                        }
                    } else {
                        Log.w(TAG, "User object is null for UID: " + currentUser.getUid());
                        isUserAuthorizedForWrite = false;
                    }
                } else {
                    Log.w(TAG, "User data not found for UID: " + currentUser.getUid());
                    isUserAuthorizedForWrite = false;
                }
                updateWriteAccessUI(isUserAuthorizedForWrite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load user role: " + error.getMessage());
                Toast.makeText(Test.this, "Lỗi tải thông tin người dùng.", Toast.LENGTH_SHORT).show();
                isUserAuthorizedForWrite = false;
                updateWriteAccessUI(false);
            }
        });
    }

    /**
     * Updates the UI elements based on user's write access authorization.
     * @param authorized True if user has access, false otherwise.
     */
    private void updateWriteAccessUI(boolean authorized) {
        MenuItem writeMenuItem = mbottomNavigationView.getMenu().findItem(R.id.nav_write);
        if (writeMenuItem != null) {
            writeMenuItem.setVisible(authorized); // Ẩn/hiện mục nav_write
            writeMenuItem.setEnabled(authorized); // Vô hiệu hóa/kích hoạt mục nav_write
        }

        mView.setUserInputEnabled(authorized); // Vô hiệu hóa/kích hoạt vuốt ViewPager2

        // Nếu người dùng không có quyền và đang ở trang Write, chuyển về trang Home
        if (!authorized && mView.getCurrentItem() == 3) { // Giả định index của nav_write là 3
            mView.setCurrentItem(0); // Chuyển về trang Home
            mbottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
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
        // Kiểm tra lại quyền mỗi khi Activity resume (để cập nhật nếu role thay đổi)
        checkUserRoleAndAuthorize();
    }
}
