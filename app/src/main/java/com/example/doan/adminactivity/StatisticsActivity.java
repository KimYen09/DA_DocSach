package com.example.doan.adminactivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.model.Story;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Imports for MPAndroidChart
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart; // Import LineChart
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry; // For LineChart
import com.github.mikephil.charting.data.LineData; // For LineChart
import com.github.mikephil.charting.data.LineDataSet; // For LineChart
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap; // For sorted map

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private TextView tvTotalStories;
    private TextView tvTotalViews;
    private TextView tvTotalRevenue;
    private TextView tvTotalUsers;
    private ProgressBar progressBarStatistics;
    private MaterialButton btnRefreshStatistics;

    private PieChart pieChartOverall; // Biểu đồ tròn tổng quan
    private BarChart barChartRevenueOverPeriod; // Biểu đồ cột doanh thu trong khoảng thời gian đã chọn
    private LineChart lineChartDailyStats; // Biểu đồ đường thống kê hàng ngày

    private EditText etFromDate;
    private EditText etToDate;
    private MaterialButton btnApplyFilter;
    private ImageView btnBack;

    private DatabaseReference storiesRef;
    private DatabaseReference giaoDichRef;
    private DatabaseReference usersRef;

    // Date format for display/input (dd/MM/yyyy)
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    // Date format for creationDate from Firebase (yyyy-MM-dd)
    private SimpleDateFormat firebaseCreationDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    // Date format for purchaseDate from Firebase (dd/MM/yyyy HH:mm)
    private SimpleDateFormat firebasePurchaseDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Ánh xạ các View
        tvTotalStories = findViewById(R.id.tvTotalStories);
        tvTotalViews = findViewById(R.id.tvTotalViews);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        progressBarStatistics = findViewById(R.id.progressBarStatistics);
        btnRefreshStatistics = findViewById(R.id.btnRefreshStatistics);

        pieChartOverall = findViewById(R.id.pieChartOverall); // Ánh xạ PieChart mới
        barChartRevenueOverPeriod = findViewById(R.id.barChartRevenueOverPeriod); // Ánh xạ BarChart mới
        lineChartDailyStats = findViewById(R.id.lineChartDailyStats); // Ánh xạ LineChart mới

        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo Firebase Database Reference
        storiesRef = FirebaseDatabase.getInstance().getReference("stories");
        giaoDichRef = FirebaseDatabase.getInstance().getReference("giaoDich");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Cấu hình biểu đồ ban đầu
        setupPieChartOverall();
        setupBarChartRevenueOverPeriod();
        setupLineChartDailyStats();

        // Đặt ngày mặc định là 30 ngày gần nhất cho EditTexts
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        etToDate.setText(displayDateFormat.format(today));
        calendar.add(Calendar.DAY_OF_MONTH, -30); // Lùi lại 30 ngày
        Date thirtyDaysAgo = calendar.getTime();
        etFromDate.setText(displayDateFormat.format(thirtyDaysAgo));

        // Tải dữ liệu thống kê ban đầu (với bộ lọc mặc định)
        loadStatistics(etFromDate.getText().toString(), etToDate.getText().toString());

        // Thiết lập OnClickListener cho các trường chọn ngày
        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate));
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate));

        // Thiết lập OnClickListeners cho nút làm mới và áp dụng bộ lọc
        btnRefreshStatistics.setOnClickListener(v -> loadStatistics(etFromDate.getText().toString(), etToDate.getText().toString()));
        btnApplyFilter.setOnClickListener(v -> loadStatistics(etFromDate.getText().toString(), etToDate.getText().toString()));

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Displays a DatePickerDialog to select a date for the given EditText.
     * @param editText The EditText to update with the selected date.
     */
    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date currentDisplayedDate = displayDateFormat.parse(editText.getText().toString());
            calendar.setTime(currentDisplayedDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing current date from EditText for DatePicker: " + e.getMessage() + ". Using current system date.");
            // If parsing fails, use the current system date for the DatePicker
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    editText.setText(displayDateFormat.format(selectedDate.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Configures the PieChart for displaying overall statistics (Views, Stories, Users).
     */
    private void setupPieChartOverall() {
        pieChartOverall.setUsePercentValues(true);
        pieChartOverall.getDescription().setEnabled(false);
        pieChartOverall.setExtraOffsets(5f, 10f, 5f, 5f);
        pieChartOverall.setDragDecelerationFrictionCoef(0.95f);
        pieChartOverall.setDrawHoleEnabled(true);
        pieChartOverall.setHoleColor(Color.WHITE);
        pieChartOverall.setTransparentCircleColor(Color.WHITE);
        pieChartOverall.setTransparentCircleAlpha(110);
        pieChartOverall.setHoleRadius(58f);
        pieChartOverall.setTransparentCircleRadius(61f);
        pieChartOverall.setDrawCenterText(true);
        pieChartOverall.setCenterText("Tổng quan");
        pieChartOverall.setRotationAngle(0);
        pieChartOverall.setRotationEnabled(true);
        pieChartOverall.setHighlightPerTapEnabled(true);
        pieChartOverall.animateY(1400);
        pieChartOverall.getLegend().setEnabled(true);
        pieChartOverall.getLegend().setTextSize(12f);
    }

    /**
     * Configures the BarChart for displaying revenue over the selected period.
     */
    private void setupBarChartRevenueOverPeriod() {
        barChartRevenueOverPeriod.setDrawBarShadow(false);
        barChartRevenueOverPeriod.setDrawValueAboveBar(true);
        barChartRevenueOverPeriod.setMaxVisibleValueCount(15); // Allow more bars for daily data
        barChartRevenueOverPeriod.setPinchZoom(true); // Enable pinch zoom for scrolling
        barChartRevenueOverPeriod.setScaleXEnabled(true); // Enable X-axis scaling/scrolling
        barChartRevenueOverPeriod.setDrawGridBackground(false);

        XAxis xAxis = barChartRevenueOverPeriod.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setLabelRotationAngle(-45); // Rotate labels for dates

        barChartRevenueOverPeriod.getAxisLeft().setDrawGridLines(true);
        barChartRevenueOverPeriod.getAxisLeft().setAxisMinimum(0f);
        barChartRevenueOverPeriod.getAxisLeft().setTextSize(12f);
        barChartRevenueOverPeriod.getAxisLeft().setTextColor(Color.BLACK);

        barChartRevenueOverPeriod.getAxisRight().setEnabled(false);
        barChartRevenueOverPeriod.getLegend().setEnabled(false);

        Description description = new Description();
        description.setText("Doanh thu theo ngày"); // Updated description
        barChartRevenueOverPeriod.setDescription(description);

        barChartRevenueOverPeriod.animateY(1500);
    }

    /**
     * Configures the LineChart for displaying daily statistics (Views, Stories, Revenue, Users).
     */
    private void setupLineChartDailyStats() {
        lineChartDailyStats.getDescription().setEnabled(false);
        lineChartDailyStats.setTouchEnabled(true);
        lineChartDailyStats.setPinchZoom(true);
        lineChartDailyStats.setDrawGridBackground(false);

        XAxis xAxis = lineChartDailyStats.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setLabelRotationAngle(-45);

        lineChartDailyStats.getAxisLeft().setDrawGridLines(true);
        lineChartDailyStats.getAxisLeft().setTextSize(12f);
        lineChartDailyStats.getAxisLeft().setTextColor(Color.BLACK);

        lineChartDailyStats.getAxisRight().setEnabled(false);
        lineChartDailyStats.getLegend().setEnabled(true);
        lineChartDailyStats.getLegend().setTextSize(12f);

        Description description = new Description();
        description.setText("Thống kê tổng hợp theo ngày");
        lineChartDailyStats.setDescription(description);

        lineChartDailyStats.animateX(1500);
    }

    /**
     * Loads statistics data from Firebase based on the selected date range.
     * This method now fetches:
     * - Daily story counts
     * - Overall total views
     * - Daily revenue
     * - Overall total user accounts (regardless of join date for now, but can be filtered)
     * And updates the UI elements and charts accordingly.
     *
     * @param fromDateStr Start date string (dd/MM/yyyy).
     * @param toDateStr End date string (dd/MM/yyyy).
     */
    private void loadStatistics(String fromDateStr, String toDateStr) {
        progressBarStatistics.setVisibility(View.VISIBLE);
        pieChartOverall.setVisibility(View.GONE);
        barChartRevenueOverPeriod.setVisibility(View.GONE);
        lineChartDailyStats.setVisibility(View.GONE);

        // Convert date strings from EditText to Date objects for comparison
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = displayDateFormat.parse(fromDateStr);
            toDate = displayDateFormat.parse(toDateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(toDate);
            cal.add(Calendar.DAY_OF_MONTH, 1); // Add one day to include the entire end date selected
            toDate = cal.getTime();
        } catch (ParseException e) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ. Vui lòng sử dụng dd/MM/yyyy.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error parsing input date fields: " + e.getMessage());
            progressBarStatistics.setVisibility(View.GONE);
            return;
        }

        final Date finalFromDate = fromDate;
        final Date finalToDate = toDate;

        // Maps to store daily counts/sums
        final Map<String, Long> dailyStoriesCount = new TreeMap<>();
        final Map<String, Long> dailyRevenue = new TreeMap<>();
        final Map<String, Long> dailyViews = new TreeMap<>(); // For LineChart
        final Map<String, Long> dailyUsersJoined = new TreeMap<>(); // For LineChart

        // Initialize maps for all days in the range to 0
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(finalFromDate);
        while (currentCal.getTime().before(finalToDate)) {
            String dateKey = firebaseCreationDateFormat.format(currentCal.getTime());
            dailyStoriesCount.put(dateKey, 0L);
            dailyRevenue.put(dateKey, 0L);
            dailyViews.put(dateKey, 0L);
            dailyUsersJoined.put(dateKey, 0L);
            currentCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // --- Fetch Total Users ---
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalUsersOverall = 0;
                if (snapshot.exists()) {
                    totalUsersOverall = snapshot.getChildrenCount(); // Count direct children of "users" node
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String joinDateStr = userSnapshot.child("joinDate").getValue(String.class);
                        if (joinDateStr != null && !joinDateStr.isEmpty()) {
                            try {
                                Date joinDate = displayDateFormat.parse(joinDateStr); // Assuming joinDate is dd/MM/yyyy
                                // We don't filter totalUsersOverall by date, but dailyUsersJoined is filtered
                                if (!joinDate.before(finalFromDate) && joinDate.before(finalToDate)) {
                                    String dateKey = firebaseCreationDateFormat.format(joinDate);
                                    dailyUsersJoined.put(dateKey, dailyUsersJoined.getOrDefault(dateKey, 0L) + 1);
                                }
                            } catch (ParseException e) {
                                Log.e(TAG, "Error parsing joinDate of user: '" + joinDateStr + "'. Error: " + e.getMessage());
                            }
                        }
                    }
                }
                tvTotalUsers.setText(String.valueOf(totalUsersOverall));

                // Now proceed to fetch stories and transactions
                fetchStoriesAndTransactions(finalFromDate, finalToDate, dailyStoriesCount, dailyRevenue, dailyViews, dailyUsersJoined, totalUsersOverall);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error loading total users: " + error.getMessage());
                Toast.makeText(StatisticsActivity.this, "Lỗi tải tổng số tài khoản: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // Even if user fetch fails, try to load other stats
                fetchStoriesAndTransactions(finalFromDate, finalToDate, dailyStoriesCount, dailyRevenue, dailyViews, new TreeMap<>(), 0); // Pass empty map and 0 for users
            }
        });
    }

    /**
     * Helper method to fetch stories and transaction data after total users are fetched.
     */
    private void fetchStoriesAndTransactions(final Date finalFromDate, final Date finalToDate,
                                             final Map<String, Long> dailyStoriesCount,
                                             final Map<String, Long> dailyRevenue,
                                             final Map<String, Long> dailyViews,
                                             final Map<String, Long> dailyUsersJoined, // Receive dailyUsersJoined here
                                             final long totalUsersOverall) {
        // Load story data
        storiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalStoriesPublishedOverall = 0;
                long totalViewsOverall = 0; // This will be the sum of daily views

                if (snapshot.exists()) {
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        Story story = storySnapshot.getValue(Story.class);
                        if (story != null) {
                            String creationDateStr = story.getCreationDate();
                            if (creationDateStr != null && !creationDateStr.isEmpty()) {
                                try {
                                    Date creationDate = firebaseCreationDateFormat.parse(creationDateStr);
                                    if (!creationDate.before(finalFromDate) && creationDate.before(finalToDate)) {
                                        // Increment daily story count
                                        String dateKey = firebaseCreationDateFormat.format(creationDate);
                                        dailyStoriesCount.put(dateKey, dailyStoriesCount.getOrDefault(dateKey, 0L) + 1);
                                        totalStoriesPublishedOverall++;

                                        Long viewCount = storySnapshot.child("viewCount").getValue(Long.class);
                                        if (viewCount != null) {
                                            dailyViews.put(dateKey, dailyViews.getOrDefault(dateKey, 0L) + viewCount);
                                            totalViewsOverall += viewCount;
                                        }
                                    }
                                } catch (ParseException e) {
                                    Log.e(TAG, "Error parsing creationDate of story: '" + creationDateStr + "'. Please check Firebase format (yyyy-MM-dd). Error: " + e.getMessage());
                                }
                            } else {
                                Log.w(TAG, "Story ID: " + storySnapshot.getKey() + " has null or empty creationDate. Skipping for date filter.");
                            }
                        }
                    }
                }

                final long finalTotalStoriesPublished = totalStoriesPublishedOverall;
                final long finalTotalViews = totalViewsOverall;

                // Load revenue data
                giaoDichRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot giaoDichSnapshot) {
                        long totalRevenueOverall = 0;
                        if (giaoDichSnapshot.exists()) {
                            for (DataSnapshot userPurchasesSnapshot : giaoDichSnapshot.getChildren()) {
                                for (DataSnapshot purchaseSnapshot : userPurchasesSnapshot.getChildren()) {
                                    String purchaseDateStr = purchaseSnapshot.child("purchaseDate").getValue(String.class);
                                    String priceStr = purchaseSnapshot.child("packagePrice").getValue(String.class); // Use packagePrice as per your data

                                    if (purchaseDateStr != null && !purchaseDateStr.isEmpty() && priceStr != null && !priceStr.isEmpty()) {
                                        try {
                                            Date purchaseDate = firebasePurchaseDateFormat.parse(purchaseDateStr);

                                            if (!purchaseDate.before(finalFromDate) && purchaseDate.before(finalToDate)) {
                                                String cleanedPriceStr = priceStr.replaceAll("[^\\d]", "");
                                                long revenueToday = Long.parseLong(cleanedPriceStr); // Use Long.parseLong for price
                                                dailyRevenue.put(firebaseCreationDateFormat.format(purchaseDate), dailyRevenue.getOrDefault(firebaseCreationDateFormat.format(purchaseDate), 0L) + revenueToday);
                                                totalRevenueOverall += revenueToday;
                                            }
                                        } catch (ParseException e) {
                                            Log.e(TAG, "Error parsing purchaseDate: '" + purchaseDateStr + "'. Please check Firebase format (dd/MM/yyyy HH:mm). Error: " + e.getMessage());
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, "Error parsing price: '" + priceStr + "'. Check if it contains non-numeric characters. Error: " + e.getMessage());
                                        }
                                    } else {
                                        Log.w(TAG, "Purchase record has null/empty purchaseDate or price. Key: " + purchaseSnapshot.getKey() + ". Skipping for revenue calculation.");
                                    }
                                }
                            }
                        }

                        // Update TextViews with overall totals
                        tvTotalStories.setText(String.valueOf(finalTotalStoriesPublished));
                        tvTotalViews.setText(String.valueOf(finalTotalViews));
                        tvTotalRevenue.setText(String.format(Locale.getDefault(), "%,d VNĐ", totalRevenueOverall));

                        // Update charts
                        updatePieChartOverall(finalTotalViews, finalTotalStoriesPublished); // Updated PieChart call
                        updateBarChartRevenueOverPeriod(dailyRevenue, finalFromDate, finalToDate); // BarChart for daily revenue
                        updateLineChartDailyStats(dailyViews, dailyStoriesCount, dailyRevenue, dailyUsersJoined); // LineChart for daily stats

                        progressBarStatistics.setVisibility(View.GONE);
                        pieChartOverall.setVisibility(View.VISIBLE);
                        barChartRevenueOverPeriod.setVisibility(View.VISIBLE);
                        lineChartDailyStats.setVisibility(View.VISIBLE);
                        Toast.makeText(StatisticsActivity.this, "Đã tải thống kê mới nhất.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBarStatistics.setVisibility(View.GONE);
                        pieChartOverall.setVisibility(View.GONE);
                        barChartRevenueOverPeriod.setVisibility(View.GONE);
                        lineChartDailyStats.setVisibility(View.GONE);
                        Toast.makeText(StatisticsActivity.this, "Lỗi tải doanh thu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Firebase error loading revenue: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarStatistics.setVisibility(View.GONE);
                pieChartOverall.setVisibility(View.GONE);
                barChartRevenueOverPeriod.setVisibility(View.GONE);
                lineChartDailyStats.setVisibility(View.GONE);
                Toast.makeText(StatisticsActivity.this, "Lỗi tải truyện: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firebase error loading stories: " + error.getMessage());
            }
        });
    }

    /**
     * Updates the PieChart with overall statistics (Views, Stories).
     * @param totalViews The total view count in the selected period.
     * @param totalStories The total count of stories published in the selected period.
     */
    private void updatePieChartOverall(long totalViews, long totalStories) { // Removed totalUsers
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (totalViews > 0) {
            entries.add(new PieEntry(totalViews, "Lượt đọc"));
        }
        if (totalStories > 0) {
            entries.add(new PieEntry(totalStories, "Số truyện"));
        }
        // totalUsers is no longer part of the PieChart as per new requirement

        if (entries.isEmpty()) {
            pieChartOverall.clear();
            pieChartOverall.setCenterText("Không có dữ liệu tổng quan.");
            pieChartOverall.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // Adjusted colors for 2 slices
        final int[] PIE_COLORS = {
                Color.parseColor("#FFC107"), // Yellow for Views
                Color.parseColor("#9E9E9E")  // Gray for Total Stories
        };
        dataSet.setColors(PIE_COLORS);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChartOverall));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChartOverall.setData(data);
        pieChartOverall.invalidate();
    }

    /**
     * Updates the BarChart with revenue for the selected period, showing daily data.
     * @param dailyRevenue A map where keys are date strings (yyyy-MM-dd) and values are revenue for that day.
     * @param finalFromDate The start date of the overall filter.
     * @param finalToDate The end date of the overall filter.
     */
    private void updateBarChartRevenueOverPeriod(Map<String, Long> dailyRevenue, Date finalFromDate, Date finalToDate) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        // Iterate through all days in the selected range to populate entries and labels
        Calendar cal = Calendar.getInstance();
        cal.setTime(finalFromDate);
        int i = 0;
        while (cal.getTime().before(finalToDate)) {
            String dateKey = firebaseCreationDateFormat.format(cal.getTime());
            long revenue = dailyRevenue.getOrDefault(dateKey, 0L);
            entries.add(new BarEntry(i, revenue));
            // Format labels to dd/MM for better readability on chart
            xAxisLabels.add(displayDateFormat.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
            i++;
        }

        if (entries.isEmpty() || entries.stream().allMatch(entry -> entry.getY() == 0)) {
            barChartRevenueOverPeriod.clear();
            barChartRevenueOverPeriod.setNoDataText("Không có dữ liệu doanh thu trong khoảng thời gian này.");
            barChartRevenueOverPeriod.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setBarBorderColor(Color.BLACK);
        dataSet.setBarBorderWidth(0.5f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        barChartRevenueOverPeriod.setData(barData);

        barChartRevenueOverPeriod.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        barChartRevenueOverPeriod.getXAxis().setLabelCount(xAxisLabels.size()); // Ensure all labels are shown
        barChartRevenueOverPeriod.getXAxis().setGranularity(1f);
        barChartRevenueOverPeriod.getXAxis().setLabelRotationAngle(-45);

        // Adjust visible range for scrolling if many bars
        if (xAxisLabels.size() > 7) { // If more than 7 days, show only 7 at a time initially
            barChartRevenueOverPeriod.setVisibleXRangeMaximum(7);
            barChartRevenueOverPeriod.moveViewToX(xAxisLabels.size() - 7); // Move to show the latest 7 days
        } else {
            barChartRevenueOverPeriod.fitScreen(); // Fit all if few bars
        }

        barChartRevenueOverPeriod.invalidate();
    }

    /**
     * Updates the LineChart with daily statistics for Views, Stories, Revenue, and Users.
     * @param dailyViews Map of daily view counts.
     * @param dailyStories Map of daily story counts.
     * @param dailyRevenue Map of daily revenue.
     * @param dailyUsersJoined Map of daily new user counts.
     */
    private void updateLineChartDailyStats(Map<String, Long> dailyViews, Map<String, Long> dailyStories,
                                           Map<String, Long> dailyRevenue, Map<String, Long> dailyUsersJoined) {
        ArrayList<Entry> viewsEntries = new ArrayList<>();
        ArrayList<Entry> storiesEntries = new ArrayList<>();
        ArrayList<Entry> revenueEntries = new ArrayList<>();
        ArrayList<Entry> usersEntries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        // Combine all unique dates from all daily maps and sort them
        Map<String, Long> allDates = new TreeMap<>();
        allDates.putAll(dailyViews);
        allDates.putAll(dailyStories);
        allDates.putAll(dailyRevenue);
        allDates.putAll(dailyUsersJoined);

        int i = 0;
        for (String dateKey : allDates.keySet()) {
            long views = dailyViews.getOrDefault(dateKey, 0L);
            long stories = dailyStories.getOrDefault(dateKey, 0L);
            long revenue = dailyRevenue.getOrDefault(dateKey, 0L);
            long users = dailyUsersJoined.getOrDefault(dateKey, 0L);

            viewsEntries.add(new Entry(i, views));
            storiesEntries.add(new Entry(i, stories));
            revenueEntries.add(new Entry(i, revenue));
            usersEntries.add(new Entry(i, users));

            try {
                Date date = firebaseCreationDateFormat.parse(dateKey);
                xAxisLabels.add(displayDateFormat.format(date)); // Label as dd/MM/yyyy
            } catch (ParseException e) {
                xAxisLabels.add(dateKey); // Fallback
            }
            i++;
        }

        if (viewsEntries.isEmpty() && storiesEntries.isEmpty() && revenueEntries.isEmpty() && usersEntries.isEmpty()) {
            lineChartDailyStats.clear();
            lineChartDailyStats.setNoDataText("Không có dữ liệu thống kê hàng ngày trong khoảng thời gian này.");
            lineChartDailyStats.invalidate();
            return;
        }

        LineDataSet viewsDataSet = new LineDataSet(viewsEntries, "Lượt đọc");
        viewsDataSet.setColor(Color.parseColor("#FFC107")); // Yellow
        viewsDataSet.setCircleColor(Color.parseColor("#FFC107"));
        viewsDataSet.setLineWidth(2f);
        viewsDataSet.setCircleRadius(3f);
        viewsDataSet.setDrawValues(false); // Hide values on line

        LineDataSet storiesDataSet = new LineDataSet(storiesEntries, "Số truyện");
        storiesDataSet.setColor(Color.parseColor("#9E9E9E")); // Gray
        storiesDataSet.setCircleColor(Color.parseColor("#9E9E9E"));
        storiesDataSet.setLineWidth(2f);
        storiesDataSet.setCircleRadius(3f);
        storiesDataSet.setDrawValues(false);

        LineDataSet revenueDataSet = new LineDataSet(revenueEntries, "Doanh thu");
        revenueDataSet.setColor(Color.parseColor("#03A9F4")); // Blue
        revenueDataSet.setCircleColor(Color.parseColor("#03A9F4"));
        revenueDataSet.setLineWidth(2f);
        revenueDataSet.setCircleRadius(3f);
        revenueDataSet.setDrawValues(false);

        LineDataSet usersDataSet = new LineDataSet(usersEntries, "Tài khoản");
        usersDataSet.setColor(Color.parseColor("#FF5722")); // Orange
        usersDataSet.setCircleColor(Color.parseColor("#FF5722"));
        usersDataSet.setLineWidth(2f);
        usersDataSet.setCircleRadius(3f);
        usersDataSet.setDrawValues(false);

        LineData lineData = new LineData(viewsDataSet, storiesDataSet, revenueDataSet, usersDataSet);
        lineChartDailyStats.setData(lineData);

        lineChartDailyStats.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        lineChartDailyStats.getXAxis().setLabelCount(xAxisLabels.size());
        lineChartDailyStats.getXAxis().setGranularity(1f);
        lineChartDailyStats.getXAxis().setLabelRotationAngle(-45);

        lineChartDailyStats.invalidate();
    }
}
