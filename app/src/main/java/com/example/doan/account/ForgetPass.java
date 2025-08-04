package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPass extends AppCompatActivity {
    private EditText edForgetEmail;
    private Button btnNhanMa;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ view
        edForgetEmail = findViewById(R.id.edforgetE);
        btnNhanMa = findViewById(R.id.btnnhanma);

        findViewById(R.id.backforget).setOnClickListener(v -> finish());

        btnNhanMa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edForgetEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgetPass.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPass.this, "Mã xác thực đã gửi đến email!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgetPass.this, Login.class)); // Chuyển về màn hình đăng nhập
                                finish();
                            } else {
                                Toast.makeText(ForgetPass.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
