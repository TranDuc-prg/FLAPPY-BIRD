package com.example.flap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button loginBtn, goRegisterBtn;
    private FirebaseAuth mAuth;
    private static final String ADMIN_EMAIL = "admin@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.et_email);
        passwordEt = findViewById(R.id.et_password);
        loginBtn = findViewById(R.id.btn_login);
        goRegisterBtn = findViewById(R.id.btn_register);

        // Nút Đăng nhập
        loginBtn.setOnClickListener(v -> handleLogin());

        // Nút Đăng ký
        goRegisterBtn.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.getEmail() != null) {
                            if (ADMIN_EMAIL.equals(user.getEmail())) {
                                startActivity(new Intent(LoginActivity.this, GameManagementActivity.class));
                                Toast.makeText(this, "Xin chào Quản lý!", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                                Toast.makeText(this, "Xin chào Người chơi!", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}