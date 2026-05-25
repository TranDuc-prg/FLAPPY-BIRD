package com.example.flap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// Lớp trừu tượng này cung cấp các thuộc tính và phương thức chung cho các đối tượng game.
// Các lớp con sẽ kế thừa từ đây.
public abstract class BaseGameObject implements GameObject {
    protected float x, y;
    protected int width, height;
    protected Rect rect;

    // Các phương thức trừu tượng phải được triển khai bởi các lớp con.
    @Override
    public abstract void update();

    @Override
    public abstract void draw(Canvas canvas, Paint paint);

    // Phương thức chung cho tất cả các đối tượng game.
    @Override
    public Rect getRect() {
        return rect;
    }

    // Kiểm tra xem đối tượng đã ra khỏi màn hình chưa.
    public boolean isOffScreen() {
        return x + width < 0;
    }
}
