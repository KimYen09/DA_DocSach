package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;

public class OTPPass extends AppCompatActivity {

    ImageView backforget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otppass);

        backforget = findViewById(R.id.backforget);

        backforget.setOnClickListener(view -> {
            Intent itback = new Intent(OTPPass.this, ForgetPass.class);
            startActivity(itback);
        });

    }
}