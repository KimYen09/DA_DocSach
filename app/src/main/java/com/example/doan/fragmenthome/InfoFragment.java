//package com.example.doan.fragmenthome;
//
//import androidx.fragment.app.Fragment;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.Button;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import com.example.doan.premium.LichSuMuaHangActivity;
//import com.bumptech.glide.Glide;
//import com.example.doan.R;
//import com.example.doan.account.EditProfile;
//import com.example.doan.account.ForgetPass;
//import com.example.doan.account.HomePage;
//import com.example.doan.model.User;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class InfoFragment extends Fragment {
//    private ImageView imgAvatar;
//    private TextView txtUsername, txtEmail, txtJoinDate, txtStoryCount, txtLikes;
//    private Button btnEditProfile, btnChangePassword, btnLogout, btnPurchaseRecord;
//    private Switch switchDarkMode;
//    private DatabaseReference userRef;
//    private FirebaseAuth auth;
//    private FirebaseUser currentUser;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_info, container, false);
//
//        // Ánh xạ các thành phần giao diện
//        imgAvatar = view.findViewById(R.id.imgAvatar);
//        txtUsername = view.findViewById(R.id.txtUsername);
//        txtEmail = view.findViewById(R.id.txtEmail);
//        txtJoinDate = view.findViewById(R.id.txtJoinDate);
//        txtStoryCount = view.findViewById(R.id.txtStoryCount);
//        txtLikes = view.findViewById(R.id.txtLikes);
//        btnEditProfile = view.findViewById(R.id.btnEditProfile);
//        btnChangePassword = view.findViewById(R.id.btnChangePassword);
//        btnLogout = view.findViewById(R.id.btnLogout);
//        switchDarkMode = view.findViewById(R.id.switchDarkMode);
//
//        btnPurchaseRecord = view.findViewById(R.id.btnPurchaseRecord);
//        // Khởi tạo Firebase Auth và lấy người dùng hiện tại
//        auth = FirebaseAuth.getInstance();
//        currentUser = auth.getCurrentUser();
//
//        if (currentUser != null) {
//            // Truyền tham chiếu đến Firebase Realtime Database cho người dùng hiện tại
//            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
//            loadUserData();  // Tải dữ liệu người dùng
//        }
//
//        // Xử lý sự kiện
//        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfile.class)));
//        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ForgetPass.class)));
//        btnLogout.setOnClickListener(v -> logout());
//
//        btnPurchaseRecord.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), LichSuMuaHangActivity.class);
//            startActivity(intent);
//        });
//
//        return view;
//    }
//
//    private void loadUserData() {
//        // Lắng nghe sự thay đổi dữ liệu người dùng từ Firebase
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    User user = snapshot.getValue(User.class);
//                    if (user != null) {
//                        // Hiển thị dữ liệu lên các TextView
//                        txtUsername.setText(user.getUsername());
//                        txtEmail.setText(user.getEmail());
//                        txtJoinDate.setText("Tham gia: " + user.getJoinDate());
//                        txtStoryCount.setText("Số truyện: " + user.getStoryCount());
//                        txtLikes.setText("Số lượt thích: " + user.getLikes());
//
//                        // Nếu có ảnh avatar, sử dụng Glide để tải ảnh vào ImageView
//                        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
//                            Glide.with(getContext()).load(user.getAvatar()).into(imgAvatar);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Hàm đăng xuất
//    private void logout() {
//        auth.signOut();
//        startActivity(new Intent(getActivity(), HomePage.class));
//        getActivity().finish();
//    }
//}
package com.example.doan.fragmenthome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri; // Vẫn giữ Uri nếu bạn muốn giữ onActivityResult để xử lý các request khác (nhưng không dùng cho avatar)
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText; // Import EditText cho AlertDialog
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // Giữ Glide nếu bạn có thể cần tải URL ảnh khác trong ứng dụng
import com.example.doan.R;
import com.example.doan.account.EditProfile;
import com.example.doan.account.ForgetPass;
import com.example.doan.account.Login;
import com.example.doan.premium.LichSuMuaHangActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// KHÔNG CẦN import FirebaseStorage và StorageReference nữa vì không dùng Storage
// import com.google.firebase.storage.FirebaseStorage;
// import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";
    // KHÔNG CẦN PICK_IMAGE_REQUEST nữa vì không mở gallery để chọn ảnh
    // private static final int PICK_IMAGE_REQUEST = 1;
    private static final int EDIT_PROFILE_REQUEST = 2; // Request code để quay lại từ EditProfile
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DARK_MODE_KEY = "darkModeEnabled";

    // UI Elements
    private TextView txtUsername, txtEmail, txtJoinDate, txtStoryCount, txtLikes;
    private Button btnEditProfile, btnChangePassword, btnPurchaseRecord, btnLogout;
    private Switch switchDarkMode;
    CircleImageView avt;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    // KHÔNG CẦN StorageReference storageRef nữa
    // private StorageReference storageRef;
    private FirebaseUser currentUser;

    // Others
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false); // Đảm bảo đúng tên layout của bạn

        // Ánh xạ các View từ layout
        avt = view.findViewById(R.id.avt);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtEmail = view.findViewById(R.id.txtEmail); // ID của email trong layout của bạn
        txtJoinDate = view.findViewById(R.id.txtJoinDate);
        txtStoryCount = view.findViewById(R.id.txtStoryCount);
        txtLikes = view.findViewById(R.id.txtLikes);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnPurchaseRecord = view.findViewById(R.id.btnPurchaseRecord);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Khởi tạo Firebase instances
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // KHÔNG CẦN khởi tạo StorageReference nữa
        // storageRef = FirebaseStorage.getInstance().getReference("avatars");

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải...");
        progressDialog.setCancelable(false);

        // Khởi tạo SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // --- Cập nhật thông tin người dùng ---
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            loadUserProfile(); // Tải thông tin người dùng khi Fragment được tạo
        } else {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            btnLogout.setText("Đăng nhập");
        }

        // --- Thiết lập Listeners ---
        setupClickListeners();
        setupDarkModeSwitch();

        return view;
    }

    private void loadUserProfile() {
        if (userRef == null) {
            Log.e(TAG, "userRef is null, cannot load profile.");
            return;
        }

        progressDialog.show();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String joinDate = snapshot.child("joinDate").getValue(String.class);
                    String avatarName = snapshot.child("avatarUrl").getValue(String.class); // Lấy tên ảnh drawable

                    Long storyCount = snapshot.child("storyCount").getValue(Long.class);
                    Long likes = snapshot.child("likes").getValue(Long.class);

                    txtUsername.setText(username != null ? username : "N/A");
                    txtEmail.setText(email != null ? email : "N/A");
                    txtJoinDate.setText(joinDate != null ? "Tham gia: " + joinDate : "Tham gia: N/A");
                    txtStoryCount.setText(storyCount != null ? String.valueOf(storyCount) : "0");
                    txtLikes.setText(likes != null ? String.valueOf(likes) : "0");

                    // --- THAY ĐỔI CÁCH TẢI VÀ HIỂN THỊ ẢNH ĐẠI DIỆN TỪ DRAWABLE ---
                    if (avatarName != null && !avatarName.isEmpty() && getContext() != null) {
                        int resourceId = getContext().getResources().getIdentifier(
                                avatarName, "drawable", getContext().getPackageName());

                        if (resourceId != 0) { // Nếu tìm thấy ID tài nguyên drawable
                            avt.setImageResource(resourceId);
                        } else {
                            avt.setImageResource(R.drawable.avatar); // Ảnh mặc định nếu không tìm thấy drawable
                            Log.e(TAG, "Không tìm thấy drawable với tên: " + avatarName);
                        }
                    } else {
                        avt.setImageResource(R.drawable.avatar); // Đặt ảnh mặc định nếu không có tên drawable
                    }
                    // --- KẾT THÚC THAY ĐỔI ---

                } else {
                    Toast.makeText(getContext(), "Không tìm thấy thông tin hồ sơ.", Toast.LENGTH_SHORT).show();
                    createDefaultUserProfile(); // Tạo hồ sơ mặc định nếu chưa tồn tại
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi tải thông tin người dùng: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi tải thông tin người dùng.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    // Phương thức này tạo hồ sơ mặc định cho người dùng mới đăng ký
    private void createDefaultUserProfile() {
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        String email = currentUser.getEmail();
        String defaultUsername = "Người dùng mới";
        String defaultJoinDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", defaultUsername);
        userProfile.put("email", email);
        userProfile.put("joinDate", defaultJoinDate);
        userProfile.put("avatarUrl", "avatar"); // <-- Đặt tên file drawable mặc định ban đầu
        userProfile.put("storyCount", 0);
        userProfile.put("likes", 0);
        userProfile.put("role", "user"); // Mặc định role là "user"

        userRef.setValue(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã tạo hồ sơ mặc định.", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // Tải lại profile để hiển thị
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tạo hồ sơ mặc định.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error creating default profile: " + e.getMessage());
                });
    }

    private void setupClickListeners() {
        // Mở dialog nhập tên ảnh khi nhấp vào ảnh đại diện
        avt.setOnClickListener(v -> showChangeAvatarDialog());

        // Chuyển đến EditProfile Activity (sử dụng startActivityForResult để nhận kết quả)
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        // Chuyển đến ChangePassword Activity
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ForgetPass.class)); // Giả định ForgetPass dùng để đổi pass
        });

        // Chuyển đến LichSuMuaHangActivity
        btnPurchaseRecord.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LichSuMuaHangActivity.class));
        });

        // Đăng xuất người dùng
        btnLogout.setOnClickListener(v -> {
            if (currentUser != null) {
                mAuth.signOut();
                Toast.makeText(getContext(), "Đã đăng xuất.", Toast.LENGTH_SHORT).show();
                // Chuyển về màn hình đăng nhập và xóa stack Activity
                Intent intent = new Intent(getActivity(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            } else {
                // Nếu người dùng chưa đăng nhập, nút này có thể chuyển đến màn hình đăng nhập
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });
    }

    private void setupDarkModeSwitch() {
        boolean isDarkModeEnabled = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        switchDarkMode.setChecked(isDarkModeEnabled);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            sharedPreferences.edit().putBoolean(DARK_MODE_KEY, isChecked).apply();
            // Có thể cần getActivity().recreate(); ở đây để áp dụng theme ngay lập tức,
            // nhưng hãy cân nhắc trải nghiệm người dùng vì nó sẽ làm Activity tải lại.
        });
    }

    // --- LOGIC MỚI: HIỂN THỊ DIALOG ĐỂ NHẬP TÊN ẢNH DRAWABLE ---
    private void showChangeAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thay đổi ảnh đại diện");
        builder.setMessage("Nhập tên file ảnh (không bao gồm .png, .jpg) từ thư mục drawable của bạn:");

        final EditText input = new EditText(requireContext());
        input.setHint("Ví dụ: avatar_new");
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String drawableName = input.getText().toString().trim();
            if (drawableName.isEmpty()) {
                Toast.makeText(getContext(), "Tên ảnh không được để trống.", Toast.LENGTH_SHORT).show();
            } else {
                saveDrawableNameToDatabase(drawableName); // Gọi phương thức mới để lưu tên ảnh
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // --- LOGIC MỚI: LƯU TÊN ẢNH DRAWABLE VÀO DATABASE ---
    private void saveDrawableNameToDatabase(String drawableName) {
        if (userRef == null) return;

        progressDialog.setMessage("Đang cập nhật ảnh...");
        progressDialog.show();

        userRef.child("avatarUrl").setValue(drawableName) // Lưu tên drawable vào avatarUrl
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // Tải lại hồ sơ để cập nhật giao diện
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Lỗi cập nhật ảnh đại diện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi lưu tên drawable vào Database: " + e.getMessage());
                });
    }

    // Phương thức onActivityResult đã được sửa đổi để bỏ PICK_IMAGE_REQUEST
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            loadUserProfile();
            Toast.makeText(getContext(), "Hồ sơ đã được cập nhật.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Đảm bảo đóng ProgressDialog khi Fragment bị hủy để tránh lỗi rò rỉ bộ nhớ
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}