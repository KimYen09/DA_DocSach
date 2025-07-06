package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPPassword extends AppCompatActivity {

    private EditText edOTP;
    private Button btnVerify;
    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otppassword);

        edOTP = findViewById(R.id.edOTPass);
        btnVerify = findViewById(R.id.btnxacminh);
        mAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");

        btnVerify.setOnClickListener(v -> {
            String otp = edOTP.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(OTPPassword.this, "Nhập OTP!", Toast.LENGTH_SHORT).show();
            } else {
                verifyOTP(otp);
            }
        });
    }

    private void verifyOTP(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(OTPPassword.this, "Xác minh thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OTPPassword.this, OTPPassword.class));
                        finish();
                    } else {
                        Toast.makeText(OTPPassword.this, "OTP sai!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
