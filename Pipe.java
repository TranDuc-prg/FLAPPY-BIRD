package com.example.flap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

// Lớp Pipe kế thừa từ BaseGameObject để tái sử dụng các thuộc tính chung.
public class Pipe extends BaseGameObject {
    private int gapY, gapHeight;
    private boolean scored;
    private Rect topRect, bottomRect;

    public Pipe(int x, int y, int width, int height, int gapY, int gapHeight) {
        // Gọi constructor của lớp cha
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
        this.scored = false;
        this.topRect = new Rect(x, 0, x + width, gapY);
        this.bottomRect = new Rect(x, gapY + gapHeight, x + width, height);
        // Lưu ý: Lớp này không sử dụng rect của BaseGameObject, nhưng vẫn kế thừa để tuân thủ cấu trúc.
    }

    @Override
    public void update(float speed) {
        x -= speed;
        topRect.offset(-(int)speed, 0);
        bottomRect.offset(-(int)speed, 0);
    }

    @Override
    public void update() {
        // This method is no longer used, but kept for compatibility.
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        canvas.drawRect(topRect, paint);
        canvas.drawRect(bottomRect, paint);
    }

    @Override
    public Rect getRect() {
        return null; // Ống nước có 2 hình chữ nhật riêng biệt
    }

    public int getX() {
        return (int)x;
    }

    public int getWidth() {
        return width;
    }

    public Rect getTopRect() {
        return topRect;
    }

    public Rect getBottomRect() {
        return bottomRect;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }
}