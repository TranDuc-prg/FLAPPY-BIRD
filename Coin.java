package com.example.flap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Coin extends BaseGameObject {
    private boolean collected;
    private Bitmap coinBitmap;
    private float scaleX = 1f;
    private int frameCounter = 0;

    public Coin(Context context, int x, int topPipeBottomY, int bottomPipeTopY) {
        this.width = 80;
        this.height = 80;
        this.x = x;
        // Sửa lại cách tính toán vị trí y để coin nằm ở giữa ống nước
        this.y = topPipeBottomY + (bottomPipeTopY - topPipeBottomY) / 2 - height / 2;
        this.collected = false;

        Bitmap raw = BitmapFactory.decodeResource(context.getResources(), R.drawable.xu);
        this.coinBitmap = Bitmap.createScaledBitmap(raw, width, height, true);
        this.rect = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
    }

    @Override
    public void update() {
        // Phương thức này có thể để trống
    }

    @Override
    public void update(float speed) {
        x -= speed;
        frameCounter++;
        if (frameCounter % 5 == 0) {
            scaleX *= -1; // Đảo chiều quay
        }
        rect.offset(-(int)speed, 0);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!collected && coinBitmap != null) {
            canvas.save();
            canvas.translate(x + width / 2f, y + height / 2f);
            canvas.scale(scaleX, 1f);
            canvas.translate(-width / 2f, -height / 2f);
            canvas.drawBitmap(coinBitmap, 0, 0, paint);
            canvas.restore();
        }
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getX() {
        return (int)x;
    }

    public boolean isOffScreen() {
        return x < -width;
    }
}