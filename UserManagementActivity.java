package com.example.flap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class UserManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cần tạo một file layout XML tương ứng trong res/layout
        setContentView(R.layout.activity_manage_users);
    }
}