package com.example.doan.adminactivity; // Đảm bảo đúng package của bạn

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.google.android.material.button.MaterialButton;

// Import các lớp cần thiết từ thư viện MPAndroidChart
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate; // Để sử dụng các màu mẫu

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    // Khai báo các thành phần UI từ layout XML
    private ImageView backButton;
    private EditText etFromDate, etToDate;
    private MaterialButton btnApplyFilter, btnRefreshStatistics;
    private TextView tvTotalStories, tvTotalViews;
    private PieChart pieChart;
    private BarChart barChart;
    private ProgressBar progressBar;

    // Đối tượng Calendar để lưu trữ ngày được chọn
    private Calendar fromCalendar, toCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gán layout XML cho Activity này
        setContentView(R.layout.activity_statistics); // Đảm bảo tên file XML của bạn là activity_statistics.xml

        // Khởi tạo các View bằng cách tìm ID của chúng
        initViews();

        // Thiết lập các sự kiện lắng nghe (listeners) cho các View
        setupListeners();

        // Tải dữ liệu thống kê ban đầu khi Activity được tạo
        loadStatisticsData();
    }

    /**
     * Phương thức để khởi tạo tất cả các thành phần UI từ layout XML.
     */
    private void initViews() {
        backButton = findViewById(R.id.backforget);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnRefreshStatistics = findViewById(R.id.btnRefreshStatistics);
        tvTotalStories = findViewById(R.id.tvTotalStories);
        tvTotalViews = findViewById(R.id.tvTotalViews);
        pieChart = findViewById(R.id.pieChartStatistics);
        barChart = findViewById(R.id.barChartStoriesPublished);
        progressBar = findViewById(R.id.progressBarStatistics);

        // Khởi tạo đối tượng Calendar cho ngày bắt đầu và ngày kết thúc
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
    }

    /**
     * Phương thức để thiết lập các sự kiện lắng nghe cho các thành phần UI.
     */
    private void setupListeners() {
        // Xử lý sự kiện nhấp chuột cho nút quay lại
        backButton.setOnClickListener(v -> finish()); // Kết thúc Activity hiện tại để quay lại màn hình trước

        // Xử lý sự kiện nhấp chuột cho EditText "Từ ngày" để mở DatePickerDialog
        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate, fromCalendar));

        // Xử lý sự kiện nhấp chuột cho EditText "Đến ngày" để mở DatePickerDialog
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate, toCalendar));

        // Xử lý sự kiện nhấp chuột cho nút "Lọc"
        btnApplyFilter.setOnClickListener(v -> {
            // Logic để áp dụng bộ lọc dựa trên ngày đã chọn
            // Ví dụ: Lấy ngày từ etFromDate và etToDate, sau đó tải lại dữ liệu
            loadStatisticsData();
        });

        // Xử lý sự kiện nhấp chuột cho nút "Làm mới"
        btnRefreshStatistics.setOnClickListener(v -> {
            // Logic để làm mới toàn bộ dữ liệu thống kê
            loadStatisticsData();
        });
    }

    /**
     * Hiển thị DatePickerDialog và cập nhật EditText với ngày đã chọn.
     *
     * @param dateEditText EditText sẽ hiển thị ngày đã chọn.
     * @param calendar     Đối tượng Calendar để lưu trữ và lấy ngày.
     */
    private void showDatePickerDialog(EditText dateEditText, Calendar calendar) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            // Cập nhật đối tượng Calendar với ngày, tháng, năm đã chọn
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // Cập nhật EditText với ngày đã chọn
            updateDateInView(dateEditText, calendar);
        };

        // Tạo và hiển thị DatePickerDialog
        new DatePickerDialog(StatisticsActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Cập nhật văn bản của EditText với ngày từ đối tượng Calendar theo định dạng "dd/MM/yyyy".
     *
     * @param dateEditText EditText cần cập nhật.
     * @param calendar     Đối tượng Calendar chứa ngày.
     */
    private void updateDateInView(EditText dateEditText, Calendar calendar) {
        String myFormat = "dd/MM/yyyy"; // Định dạng ngày mong muốn
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Phương thức giả lập để tải dữ liệu thống kê.
     * Trong ứng dụng thực tế, bạn sẽ gọi API hoặc truy vấn cơ sở dữ liệu tại đây.
     */
    private void loadStatisticsData() {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị thanh tiến trình khi đang tải dữ liệu

        // Giả lập quá trình tải dữ liệu bằng cách sử dụng Handler.postDelayed
        new android.os.Handler().postDelayed(() -> {
            // Dữ liệu mẫu (thay thế bằng dữ liệu thực tế của bạn)
            int totalStories = 125;
            int totalViews = 56789;
            // Tỷ lệ lượt đọc cho các loại truyện (ví dụ: giả tưởng và phi giả tưởng)
            float fictionViewsRatio = 0.6f; // 60% truyện giả tưởng
            float nonFictionViewsRatio = 0.4f; // 40% truyện phi giả tưởng

            // Cập nhật các TextView với dữ liệu mẫu
            tvTotalStories.setText(String.valueOf(totalStories));
            tvTotalViews.setText(String.valueOf(totalViews));

            // Thiết lập dữ liệu cho biểu đồ tròn
            setupPieChart(fictionViewsRatio, nonFictionViewsRatio);
            // Thiết lập dữ liệu cho biểu đồ cột
            setupBarChart();

            progressBar.setVisibility(View.GONE); // Ẩn thanh tiến trình sau khi tải xong
        }, 2000); // Giả lập thời gian tải 2 giây
    }

    /**
     * Thiết lập và hiển thị biểu đồ tròn (Pie Chart) với dữ liệu mẫu.
     *
     * @param fictionRatio    Tỷ lệ lượt xem của truyện giả tưởng.
     * @param nonFictionRatio Tỷ lệ lượt xem của truyện phi giả tưởng.
     */
    private void setupPieChart(float fictionRatio, float nonFictionRatio) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        // Thêm các mục nhập vào biểu đồ tròn
        entries.add(new PieEntry(fictionRatio, "Truyện giả tưởng"));
        entries.add(new PieEntry(nonFictionRatio, "Truyện phi giả tưởng"));

        PieDataSet dataSet = new PieDataSet(entries, "Thống kê lượt đọc");
        // Đặt màu cho các phần của biểu đồ
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Sử dụng các màu mẫu từ MPAndroidChart
        // Hoặc bạn có thể định nghĩa màu tùy chỉnh:
        // dataSet.setColors(new int[]{Color.rgb(255, 102, 0), Color.rgb(0, 153, 204)});

        dataSet.setValueTextSize(12f); // Kích thước chữ của giá trị
        dataSet.setValueTextColor(Color.BLACK); // Màu chữ của giá trị

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setCenterText("Tổng lượt đọc"); // Văn bản ở giữa biểu đồ
        pieChart.getDescription().setEnabled(false); // Ẩn nhãn mô tả
        pieChart.animateY(1000); // Hiệu ứng động khi hiển thị biểu đồ
        pieChart.invalidate(); // Làm mới biểu đồ để hiển thị thay đổi
    }

    /**
     * Thiết lập và hiển thị biểu đồ cột (Bar Chart) với dữ liệu mẫu.
     */
    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        // Dữ liệu mẫu cho số truyện được phát hành theo tháng/quý
        // X-axis: 0=Tháng 1, 1=Tháng 2, v.v.
        // Y-axis: Số lượng truyện
        entries.add(new BarEntry(0, 10)); // Tháng 1: 10 truyện
        entries.add(new BarEntry(1, 15)); // Tháng 2: 15 truyện
        entries.add(new BarEntry(2, 8));  // Tháng 3: 8 truyện
        entries.add(new BarEntry(3, 20)); // Tháng 4: 20 truyện

        BarDataSet dataSet = new BarDataSet(entries, "Số truyện phát hành");
        // Đặt màu cho các cột
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS); // Sử dụng các màu mẫu từ MPAndroidChart
        // Hoặc bạn có thể định nghĩa màu tùy chỉnh:
        // dataSet.setColors(new int[]{Color.rgb(100, 200, 50), Color.rgb(50, 150, 250)});

        dataSet.setValueTextSize(12f); // Kích thước chữ của giá trị
        dataSet.setValueTextColor(Color.BLACK); // Màu chữ của giá trị

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false); // Ẩn nhãn mô tả
        barChart.animateY(1000); // Hiệu ứng động khi hiển thị biểu đồ
        barChart.invalidate(); // Làm mới biểu đồ để hiển thị thay đổi
    }
}
