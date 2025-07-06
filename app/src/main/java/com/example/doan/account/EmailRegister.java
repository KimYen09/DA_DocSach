package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;

public class EmailRegister extends AppCompatActivity {

    ImageView backinfo;
    Button btntt;
    EditText edtEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);


        mAuth = FirebaseAuth.getInstance();


        backinfo = findViewById(R.id.backinfo);
        btntt = findViewById(R.id.btContinue);
        edtEmail = findViewById(R.id.edEmail);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Intent intent = new Intent(EmailRegister.this, HomePage.class);
            startActivity(intent);
            finish();
        }


        backinfo.setOnClickListener(view -> {
            Intent itbackinfo = new Intent(EmailRegister.this, HomePage.class);
            startActivity(itbackinfo);
        });


        btntt.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(EmailRegister.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!isValidEmail(email)) {
                Toast.makeText(EmailRegister.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent ittt = new Intent(EmailRegister.this, PassRegister.class);
            ittt.putExtra("email", email);
            startActivity(ittt);
        });
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.indexOf("@") < email.lastIndexOf(".");
    }
}
