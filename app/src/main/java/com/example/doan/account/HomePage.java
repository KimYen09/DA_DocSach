//package com.example.doan.account;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//
//
//import com.example.doan.R;
//import com.example.doan.Test;
//import com.example.doan.fragmenthome.HomeFragment;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.material.button.MaterialButton;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class HomePage extends AppCompatActivity {
//    MaterialButton btnloginGoogle, btnlogin;
//    Button btnkhach;
//    private static final int RC_SIGN_IN = 100;
//    private GoogleSignInClient mGoogleSignInClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_page);
//        EdgeToEdge.enable(this);
//
//        // Ánh xạ
//        btnloginGoogle = findViewById(R.id.btnloginGoogle);
//        btnkhach = findViewById(R.id.btnkhach);
//        btnlogin = findViewById(R.id.btnlogin);
//
//        btnlogin.setOnClickListener(view -> {
//            Intent itlogin = new Intent(HomePage.this, Login.class);
//            startActivity(itlogin);
//        });
//
//        // Cấu hình Google Sign-In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        if (btnloginGoogle == null) {
//            Log.e("HomePage", "Button bị null! Kiểm tra ID trong XML.");
//        } else {
//            btnloginGoogle.setOnClickListener(view -> signInWithGoogle());
//        }
//
//
//        btnkhach.setOnClickListener(view -> {
//            Intent itkhach = new Intent(this, Test.class);
//            startActivity(itkhach);
//        });
//    }
//
//    private final ActivityResultLauncher<Intent> googleSignInLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    try {
//                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class);
//                        if (account != null) {
//                            String userEmail = account.getEmail();
//                            Log.d("GoogleSignIn", "Đăng nhập thành công: " + userEmail);
//                            // BỎ ĐIỀU KIỆN KIỂM TRA ADMIN
//                            Intent mainIntent = new Intent(HomePage.this, Test.class);
//                            mainIntent.putExtra("userEmail", userEmail);
//                            startActivity(mainIntent);
//                            finish();
//                        }
//                    } catch (ApiException e) {
//                        Toast.makeText(this, "Đăng nhập thất bại: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//
//
//
//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        googleSignInLauncher.launch(signInIntent);
//    }
//
//
//
//}

package com.example.doan.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.doan.R;
import com.example.doan.Test;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    MaterialButton btnloginGoogle, btnlogin;
    Button btnkhach;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        EdgeToEdge.enable(this);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ
        btnloginGoogle = findViewById(R.id.btnloginGoogle);
        btnkhach = findViewById(R.id.btnkhach);
        btnlogin = findViewById(R.id.btnlogin);

        btnlogin.setOnClickListener(view -> {
            Intent itlogin = new Intent(HomePage.this, Login.class);
            startActivity(itlogin);
        });

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnloginGoogle.setOnClickListener(view -> signInWithGoogle());

        btnkhach.setOnClickListener(view -> {
            Intent itkhach = new Intent(this, Test.class);
            startActivity(itkhach);
        });
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class);
                        if (account != null) {
                            Log.d("GoogleAuth", "Đăng nhập thành công: " + account.getEmail());
                            firebaseAuthWithGoogle(account.getIdToken(), account);
                        }
                    } catch (ApiException e) {
                        Log.e("GoogleAuth", "Đăng nhập thất bại", e);
                        Toast.makeText(this, "Đăng nhập thất bại: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("GoogleAuth", "Xác thực Firebase thành công");
                saveUserToFirestore(account);
            } else {
                Log.e("GoogleAuth", "Xác thực Firebase thất bại", task.getException());
                Toast.makeText(this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToFirestore(GoogleSignInAccount account) {
        String userId = auth.getCurrentUser().getUid();
        String avatarUrl = (account.getPhotoUrl() != null) ? account.getPhotoUrl().toString() : "https://example.com/default-avatar.jpg";
        String email = account.getEmail();
        String joinDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Map<String, Object> user = new HashMap<>();
        user.put("avatar", avatarUrl);
        user.put("email", email);
        user.put("joinDate", joinDate);
        user.put("likes", 0);
        user.put("role", "author");
        user.put("storyCount", 0);

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", "Người dùng đã được lưu!");
                Intent mainIntent = new Intent(HomePage.this, Test.class);
                mainIntent.putExtra("userEmail", email);
                startActivity(mainIntent);
                finish();
            } else {
                Log.e("Firestore", "Lưu dữ liệu thất bại", task.getException());
                Toast.makeText(this, "Lưu dữ liệu thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
