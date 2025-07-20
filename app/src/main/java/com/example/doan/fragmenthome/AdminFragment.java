package com.example.doan.fragmenthome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doan.R;
import com.example.doan.adminactivity.PendingStoriesActivity;
import com.google.android.material.button.MaterialButton;

// Import các Activity quản lý tương ứng nếu bạn đã có
 import com.example.doan.adminactivity.WriteActivity;
// import com.example.doan.admin.PendingStoriesActivity;
// import com.example.doan.admin.PremiumManagementActivity;
// import com.example.doan.admin.StatisticsActivity;

public class AdminFragment extends Fragment {

    private MaterialButton btnAllStories;
    private MaterialButton btnPendingStories;
    private MaterialButton btnPremiumManagement;
    private MaterialButton btnStatistics;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Ánh xạ các MaterialButton từ layout
        btnAllStories = view.findViewById(R.id.btntruyen);
        btnPendingStories = view.findViewById(R.id.btnchoduyet);
        btnPremiumManagement = view.findViewById(R.id.btnPremium);
        btnStatistics = view.findViewById(R.id.btnThongke);

        // Thiết lập OnClickListener cho từng nút
        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        // OnClickListener cho nút "Tất cả các truyện"
        if (btnAllStories != null) { // Kiểm tra null
            btnAllStories.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
            });
        }

    }
}
