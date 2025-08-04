package com.example.doan.account;

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
import com.example.doan.Test;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText edEmail, edPass;
    private Button btContinue, btnQuenPass, btnRegister;
    private ImageView backacc;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        btContinue = findViewById(R.id.btContinue);
        btnQuenPass = findViewById(R.id.btnquenPass);
        btnRegister = findViewById(R.id.btnregister);
        backacc = findViewById(R.id.backacc);

        btContinue.setOnClickListener(v -> loginUser());

        btnQuenPass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgetPass.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);
        });

        backacc.setOnClickListener(v -> finish());
    }

    private void loginUser() {
        String email = edEmail.getText().toString().trim();
        String password = edPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Vui lòng nhập email!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edPass.setError("Vui lòng nhập mật khẩu!");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, Test.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(Login.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
