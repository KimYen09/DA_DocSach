package com.example.doan.fragmenthome;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
<<<<<<< HEAD
import android.widget.Button;
=======
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

<<<<<<< HEAD
import com.example.doan.premium.LichSuMuaHangActivity;
=======
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339
import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.account.EditProfile;
import com.example.doan.account.ForgetPass;
import com.example.doan.account.HomePage;
import com.example.doan.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoFragment extends Fragment {
    private ImageView imgAvatar;
    private TextView txtUsername, txtEmail, txtJoinDate, txtStoryCount, txtLikes;
<<<<<<< HEAD
    private Button btnEditProfile, btnChangePassword, btnLogout, btnPurchaseRecord;
=======
    private Button btnEditProfile, btnChangePassword, btnLogout;
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339
    private Switch switchDarkMode;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Ánh xạ các thành phần giao diện
        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtJoinDate = view.findViewById(R.id.txtJoinDate);
        txtStoryCount = view.findViewById(R.id.txtStoryCount);
        txtLikes = view.findViewById(R.id.txtLikes);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

<<<<<<< HEAD
        btnPurchaseRecord = view.findViewById(R.id.btnPurchaseRecord);
=======
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339
        // Khởi tạo Firebase Auth và lấy người dùng hiện tại
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Truyền tham chiếu đến Firebase Realtime Database cho người dùng hiện tại
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            loadUserData();  // Tải dữ liệu người dùng
        }

        // Xử lý sự kiện
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfile.class)));
        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ForgetPass.class)));
        btnLogout.setOnClickListener(v -> logout());

<<<<<<< HEAD
        btnPurchaseRecord.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LichSuMuaHangActivity.class);
            startActivity(intent);
        });

=======
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339
        return view;
    }

    private void loadUserData() {
        // Lắng nghe sự thay đổi dữ liệu người dùng từ Firebase
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Hiển thị dữ liệu lên các TextView
                        txtUsername.setText(user.getUsername());
                        txtEmail.setText(user.getEmail());
                        txtJoinDate.setText("Tham gia: " + user.getJoinDate());
                        txtStoryCount.setText("Số truyện: " + user.getStoryCount());
                        txtLikes.setText("Số lượt thích: " + user.getLikes());

                        // Nếu có ảnh avatar, sử dụng Glide để tải ảnh vào ImageView
                        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                            Glide.with(getContext()).load(user.getAvatar()).into(imgAvatar);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm đăng xuất
    private void logout() {
        auth.signOut();
        startActivity(new Intent(getActivity(), HomePage.class));
        getActivity().finish();
    }
}
