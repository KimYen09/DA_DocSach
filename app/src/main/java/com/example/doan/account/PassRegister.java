package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;

public class PassRegister extends AppCompatActivity {

    Button btnDangKy;
    ImageView passback;
    EditText edtPassword;
    EditText edtPassword2;
    FirebaseAuth mAuth;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_register);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");

        // Anh xa các view
        edtPassword = findViewById(R.id.edPassword);
        edtPassword2 = findViewById(R.id.edPassword2);
        btnDangKy = findViewById(R.id.btnDangKy);
        passback = findViewById(R.id.passback);

        // Quay lại màn hình EmailRegister
        passback.setOnClickListener(view -> {
            Intent itpassback = new Intent(PassRegister.this, EmailRegister.class);
            startActivity(itpassback);
        });

        // Đăng ký người dùng
        btnDangKy.setOnClickListener(view -> {
            String password = edtPassword.getText().toString().trim();
            String password2 = edtPassword2.getText().toString().trim();

            // Kiểm tra mật khẩu không rỗng
            if (password.isEmpty()) {
                Toast.makeText(PassRegister.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu nhập lại có khớp không
            if (!password.equals(password2)) {
                Toast.makeText(PassRegister.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra độ dài mật khẩu (có thể tùy chỉnh độ dài)
            if (password.length() < 6) {
                Toast.makeText(PassRegister.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra tính hợp lệ của mật khẩu (bao gồm chữ hoa, chữ thường và ký tự đặc biệt)
            if (!isValidPassword(password)) {
                Toast.makeText(PassRegister.this, "Mật khẩu phải có ít nhất một chữ cái viết hoa, một chữ cái viết thường và một ký tự đặc biệt", Toast.LENGTH_LONG).show();
                return;
            }

            // Đăng ký người dùng qua Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PassRegister.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PassRegister.this, ListCategory.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() != null) {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(PassRegister.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PassRegister.this, "Đăng ký thất bại: Lỗi không xác định", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        });
    }

    private boolean isValidPassword(String password) {
        return password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
