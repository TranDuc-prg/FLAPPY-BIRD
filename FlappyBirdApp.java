package com.example.flap; // Đảm bảo package này khớp với package của bạn

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class FlappyBirdApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo Firebase ở đây để nó có thể được sử dụng bởi mọi Activity
        FirebaseApp.initializeApp(this);
    }
}