package com.example.flap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bird extends BaseGameObject {
    private float velocityY = 0;
    private float gravity = 0.4f;
    private Bitmap[] frames;
    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private final int frameLengthInMilliseconds = 100;
    private float gravityFactor;
    private int screenHeight;

    public Bird(float startX, float startY, Context context, int[] drawableResIds, float gravityFactor, int screenHeight) {
        this.x = startX;
        this.y = startY;
        this.gravityFactor = gravityFactor;
        this.screenHeight = screenHeight;

        frames = new Bitmap[drawableResIds.length];
        for (int i = 0; i < drawableResIds.length; i++) {
            Bitmap original = BitmapFactory.decodeResource(context.getResources(), drawableResIds[i]);
            frames[i] = Bitmap.createScaledBitmap(original, 50, 50, false);
        }

        this.width = frames[0].getWidth();
        this.height = frames[0].getHeight();
        this.rect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));
    }

    // Phương thức này bắt buộc phải có để thỏa mãn interface, nhưng bạn có thể để trống
    @Override
    public void update(float speed) {
        // Chim không cần cập nhật dựa trên speed của ống nước, nên phương thức này có thể trống.
    }

    @Override
    public void update() {
        velocityY += gravity * gravityFactor;
        y += velocityY * 0.7f;

        if (y < 0) y = 0;
        if (y + height > screenHeight) {
            y = screenHeight - height;
            velocityY = 0;
        }

        updateRect();
        updateFrame();
    }

    private void updateFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(frames[currentFrame], x, y, paint);
    }

    public void flap() {
        velocityY = -15;
    }

    private void updateRect() {
        rect.left = (int) x;
        rect.top = (int) y;
        rect.right = (int) (x + width);
        rect.bottom = (int) (y + height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public boolean isOffScreen() {
        return false;
    }
}