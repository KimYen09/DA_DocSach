package com.example.doan.adminactivity; // Đặt trong package admin hoặc package phù hợp

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText; // Thêm import cho EditText
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.R;
import com.example.doan.model.Story;
import com.example.doan.utils.FirebaseDataUpdater;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Imports cho MPAndroidChart
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar; // Thêm import cho Calendar
import java.util.List;
import java.util.Locale; // Thêm import cho Locale
import java.text.SimpleDateFormat; // Thêm import cho SimpleDateFormat

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private TextView tvTotalStories;
    private TextView tvTotalViews;
    private ProgressBar progressBarStatistics;
    private MaterialButton btnRefreshStatistics;
    private BarChart barChartStatistics;

    private EditText etFilterYear; // Thêm EditText cho năm
    private EditText etFilterMonth; // Thêm EditText cho tháng
    private MaterialButton btnApplyFilter; // Thêm nút áp dụng bộ lọc

    private DatabaseReference storiesRef;
    private ImageView backforget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        // Ánh xạ các View
        tvTotalStories = findViewById(R.id.tvTotalStories);
        tvTotalViews = findViewById(R.id.tvTotalViews);
        progressBarStatistics = findViewById(R.id.progressBarStatistics);
        btnRefreshStatistics = findViewById(R.id.btnRefreshStatistics);
        barChartStatistics = findViewById(R.id.barChartStatistics);
        backforget = findViewById(R.id.backforget);

        etFilterYear = findViewById(R.id.etFilterYear); // Ánh xạ EditText năm
        etFilterMonth = findViewById(R.id.etFilterMonth); // Ánh xạ EditText tháng
        btnApplyFilter = findViewById(R.id.btnApplyFilter); // Ánh xạ nút áp dụng bộ lọc

        // Xử lý WindowInsets (từ template của bạn)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Khởi tạo Firebase Database Reference
        storiesRef = FirebaseDatabase.getInstance().getReference("stories");

        // Cấu hình biểu đồ ban đầu
        setupBarChart();

        // Đặt giá trị mặc định cho năm và tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        etFilterYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        etFilterMonth.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1)); // Tháng trong Calendar bắt đầu từ 0

        // Tải dữ liệu thống kê ban đầu (với bộ lọc mặc định)
        loadStatistics(etFilterYear.getText().toString(), etFilterMonth.getText().toString());

        // Thiết lập OnClickListener cho nút làm mới và áp dụng bộ lọc
        btnRefreshStatistics.setOnClickListener(v -> loadStatistics(etFilterYear.getText().toString(), etFilterMonth.getText().toString()));
        btnApplyFilter.setOnClickListener(v -> loadStatistics(etFilterYear.getText().toString(), etFilterMonth.getText().toString()));

        backforget.setOnClickListener(view -> {
            finish();
        });
    }

    private void setupBarChart() {
        barChartStatistics.setDrawBarShadow(false);
        barChartStatistics.setDrawValueAboveBar(true);
        barChartStatistics.setMaxVisibleValueCount(50);
        barChartStatistics.setPinchZoom(false);
        barChartStatistics.setDrawGridBackground(false);

        XAxis xAxis = barChartStatistics.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setLabelCount(2);

//        // Trong setupBarChart()
        barChartStatistics.setBackgroundColor(Color.WHITE); // Màu nền của biểu đồ
        barChartStatistics.setExtraOffsets(5f, 10f, 5f, 10f); // Thêm không gian trống (left, top, right, bottom)

//        // Để thêm hiệu ứng đổ bóng cho cột (nếu muốn)
//         barChartStatistics.setDrawBarShadow(true);
//         barChartStatistics.setDrawValueAboveBar(false);

        // Để có thể phóng to/thu nhỏ
         barChartStatistics.setPinchZoom(true);
         barChartStatistics.setScaleEnabled(true);

        barChartStatistics.getAxisLeft().setDrawGridLines(true);
        barChartStatistics.getAxisLeft().setAxisMinimum(0f);
        barChartStatistics.getAxisLeft().setTextSize(12f);
        barChartStatistics.getAxisLeft().setTextColor(Color.BLACK);

        barChartStatistics.getAxisRight().setEnabled(false);
        barChartStatistics.getLegend().setEnabled(false);

        Description description = new Description();
        description.setText("");
        barChartStatistics.setDescription(description);

        barChartStatistics.animateY(1500);
    }

    // Thay đổi phương thức loadStatistics để nhận tham số lọc
    private void loadStatistics(String filterYear, String filterMonth) {
        progressBarStatistics.setVisibility(View.VISIBLE);
        barChartStatistics.setVisibility(View.GONE);

        storiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalStoriesFiltered = 0;
                long totalViewsFiltered = 0;

                if (snapshot.exists()) {
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        Story story = storySnapshot.getValue(Story.class);
                        if (story != null) {
                            // Lọc truyện theo năm và tháng tạo
                            // Giả định Story.java có trường 'creationDate' (String, định dạng yyyy-MM-dd)
                            String creationDate = story.getCreationDate(); // Cần thêm getCreationDate() vào model Story

                            boolean matchesFilter = true;

                            if (creationDate != null && !creationDate.isEmpty()) {
                                try {
                                    // Phân tích ngày tạo để so sánh với bộ lọc
                                    SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

                                    if (!filterYear.isEmpty()) {
                                        String storyYear = yearFormat.format(yearMonthFormat.parse(creationDate));
                                        if (!storyYear.equals(filterYear)) {
                                            matchesFilter = false;
                                        }
                                    }

                                    if (matchesFilter && !filterMonth.isEmpty()) {
                                        String storyMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(yearMonthFormat.parse(creationDate));
                                        // Đảm bảo tháng có 2 chữ số (ví dụ: "07" thay vì "7")
                                        String formattedFilterMonth = String.format(Locale.getDefault(), "%02d", Integer.parseInt(filterMonth));
                                        if (!storyMonth.equals(formattedFilterMonth)) {
                                            matchesFilter = false;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Lỗi phân tích ngày tạo truyện: " + creationDate + ", Lỗi: " + e.getMessage());
                                    matchesFilter = false; // Nếu có lỗi phân tích, không tính truyện này
                                }
                            } else {
                                matchesFilter = false; // Không có ngày tạo, không tính vào bộ lọc thời gian
                            }

                            if (matchesFilter) {
                                totalStoriesFiltered++; // Tăng số truyện nếu khớp bộ lọc
                                Long viewCount = storySnapshot.child("viewCount").getValue(Long.class);
                                if (viewCount != null) {
                                    totalViewsFiltered += viewCount; // Cộng lượt đọc nếu khớp bộ lọc
                                }
                            }
                        }
                    }
                }

                // Cập nhật giao diện TextViews
                tvTotalStories.setText(String.valueOf(totalStoriesFiltered));
                tvTotalViews.setText(String.valueOf(totalViewsFiltered));

                // Cập nhật biểu đồ
                updateBarChart(totalStoriesFiltered, totalViewsFiltered);

                progressBarStatistics.setVisibility(View.GONE);
                barChartStatistics.setVisibility(View.VISIBLE);
                Toast.makeText(StatisticsActivity.this, "Đã tải thống kê mới nhất.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarStatistics.setVisibility(View.GONE);
                barChartStatistics.setVisibility(View.GONE);
                Toast.makeText(StatisticsActivity.this, "Lỗi tải thống kê: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi Firebase khi tải thống kê: " + error.getMessage());
            }
        });
    }

    private void updateBarChart(long totalStories, long totalViews) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, totalStories));
        entries.add(new BarEntry(1f, totalViews));

        BarDataSet dataSet = new BarDataSet(entries, "Thống kê");
        dataSet.setColors(new int[]{
                Color.parseColor("#F69084"),
                Color.parseColor("#807C7C")
        });
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.2f);

        barChartStatistics.setData(barData);

        final String[] labels = new String[]{"Tổng truyện", "Tổng lượt đọc"};
        barChartStatistics.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        barChartStatistics.invalidate();
    }
}
