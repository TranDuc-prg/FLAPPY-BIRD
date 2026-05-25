package com.example.flap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// Define an interface for all game objects.
// All game objects must implement these methods.
public interface GameObject {
    void update(float speed); // Added update method with speed parameter
    void update();            // Kept the original update method for compatibility
    void draw(Canvas canvas, Paint paint);
    Rect getRect();
    boolean isOffScreen();
}