<<<<<<< HEAD
package com.example.doan.premium;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class Premium extends AppCompatActivity {
    private ImageView ivexit;
    private Button pre1m, pre6m, pre1y;
    private static final String goi_1_thang = "Gói 1 Tháng";
    private static final String phi_1_thang = "VND 39,000";

    private static final String goi_6_thang = "Gói 6 Tháng";
    private static final String phi_6_thang = "VND 199,000";

    private static final String goi_1_nam = "Gói 1 Năm";
    private static final String phi_1_nam = "VND 399,000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        ivexit = findViewById(R.id.ivexit);
        pre1m = findViewById(R.id.pre1m);
        pre6m = findViewById(R.id.pre6m);
        pre1y = findViewById(R.id.pre1y);
        // Nút premium 1 tháng
        pre1m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_1_thang, phi_1_thang);
            }
        });
        // Nút premium 6 tháng
        pre6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_6_thang, phi_6_thang);
            }
        });
        // Nút premium 1 tháng
        pre1y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_1_nam, phi_1_nam);
            }
        });
        // Nút exit
        ivexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void navigateToConfirmation(String ten_goi, String phi_goi) {
        Intent intent = new Intent(Premium.this, Payment.class);
        intent.putExtra("ten_goi", ten_goi);
        intent.putExtra("phi_goi", phi_goi);
        startActivity(intent);
    }
=======
package com.example.doan.premium;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class Premium extends AppCompatActivity {
    private ImageView ivexit;
    private Button pre1m, pre6m, pre1y;
    private static final String goi_1_thang = "Gói 1 Tháng";
    private static final String phi_1_thang = "VND 39,000";

    private static final String goi_6_thang = "Gói 6 Tháng";
    private static final String phi_6_thang = "VND 199,000";

    private static final String goi_1_nam = "Gói 1 Năm";
    private static final String phi_1_nam = "VND 399,000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        ivexit = findViewById(R.id.ivexit);
        pre1m = findViewById(R.id.pre1m);
        pre6m = findViewById(R.id.pre6m);
        pre1y = findViewById(R.id.pre1y);
        // Nút premium 1 tháng
        pre1m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_1_thang, phi_1_thang);
            }
        });
        // Nút premium 6 tháng
        pre6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_6_thang, phi_6_thang);
            }
        });
        // Nút premium 1 tháng
        pre1y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConfirmation(goi_1_nam, phi_1_nam);
            }
        });
        // Nút exit
        ivexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void navigateToConfirmation(String ten_goi, String phi_goi) {
        Intent intent = new Intent(Premium.this, Payment.class);
        intent.putExtra("ten_goi", ten_goi);
        intent.putExtra("phi_goi", phi_goi);
        startActivity(intent);
    }
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339
}