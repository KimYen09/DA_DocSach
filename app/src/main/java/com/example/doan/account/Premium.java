package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.R;
import com.example.doan.Test;

public class Premium extends AppCompatActivity {
    ImageView ivexit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_premium);

        ivexit = findViewById(R.id.ivexit);

        ivexit.setOnClickListener(view -> {
            Intent itexit = new Intent(Premium.this, Test.class);
            startActivity(itexit);
        });

    }
}