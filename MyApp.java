package com.example.flap;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
    }
}
