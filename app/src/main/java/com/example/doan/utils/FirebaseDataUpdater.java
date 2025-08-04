package com.example.doan.utils; // Bạn có thể tạo một package utils hoặc đặt vào một Activity tạm thời

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast; // Chỉ sử dụng nếu bạn đặt trong Activity/Fragment có Context

import com.example.doan.model.Story; // Đảm bảo import đúng lớp Story của bạn
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDataUpdater {

    private static final String TAG = "FirebaseDataUpdater";

    public static void updateAllStoriesCreationDate(android.content.Context context) { // Thêm Context nếu muốn dùng Toast
        DatabaseReference storiesRef = FirebaseDatabase.getInstance().getReference("stories");

        // Lấy ngày hiện tại định dạng "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        Log.d(TAG, "Bắt đầu cập nhật creationDate cho tất cả truyện thành: " + todayDate);

        storiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int updatedCount = 0;
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    String storyId = storySnapshot.getKey();
                    // Lấy đối tượng Story hiện tại (không bắt buộc, chỉ để kiểm tra)
                    // Story story = storySnapshot.getValue(Story.class);

                    // Tạo Map để cập nhật chỉ trường creationDate
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("creationDate", todayDate);

                    if (storyId != null) {
                        storiesRef.child(storyId).updateChildren(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Đã cập nhật creationDate cho truyện ID: " + storyId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Lỗi cập nhật creationDate cho truyện ID: " + storyId + ", Lỗi: " + e.getMessage());
                                });
                        updatedCount++;
                    }
                }
                Log.d(TAG, "Hoàn tất quá trình cập nhật. Tổng số truyện đã xử lý: " + updatedCount);
                if (context != null) {
                    Toast.makeText(context, "Đã cập nhật creationDate cho " + updatedCount + " truyện.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi Firebase khi tải dữ liệu để cập nhật: " + error.getMessage());
                if (context != null) {
                    Toast.makeText(context, "Lỗi cập nhật dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
