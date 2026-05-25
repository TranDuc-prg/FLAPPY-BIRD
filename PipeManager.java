package com.example.flap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.Context;
import java.util.ArrayList;

public class PipeManager {
    private ArrayList<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int screenHeight, screenWidth;
    private float pipeSpeed = 10;
    private float pipeGap = 450;
    private float pipeDistance = 600;
    private int pipeWidth = 150;
    private Context context;

    public PipeManager(Context context) {
        this.context = context;
        pipes = new ArrayList<>();
        coins = new ArrayList<>();
    }

    public void setScreenHeight(int h) { screenHeight = h; }
    public void setScreenWidth(int w) { screenWidth = w; }

    public void setPipeSpeed(float speed) { this.pipeSpeed = speed; }
    public void setPipeGap(float gap) { this.pipeGap = gap; }
    public void setPipeDistance(float distance) { this.pipeDistance = distance; }

    public void update() {
        for (Pipe pipe : pipes) {
            pipe.update(pipeSpeed);
        }
        for (Coin coin : coins) {
            coin.update(pipeSpeed);
        }

        if (!pipes.isEmpty() && pipes.get(0).getX() + pipeWidth < 0) {
            pipes.remove(0);
        }

        if (!coins.isEmpty() && coins.get(0).getX() + 80 < 0) {
            coins.remove(0);
        }

        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).getX() < screenWidth - pipeDistance) {
            int gapY = (int) (Math.random() * (screenHeight - pipeGap - 200)) + 100;
            pipes.add(new Pipe(screenWidth, 0, pipeWidth, screenHeight, gapY, (int) pipeGap));

            int topPipeBottomY = gapY;
            int bottomPipeTopY = gapY + (int) pipeGap;

            coins.add(new Coin(context, screenWidth + pipeWidth, topPipeBottomY, bottomPipeTopY));
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        for (Pipe pipe : pipes) {
            pipe.draw(canvas, paint);
        }
        for (Coin coin : coins) {
            coin.draw(canvas, paint);
        }
    }

    public ArrayList<Pipe> getPipes() { return pipes; }
    public ArrayList<Coin> getCoins() { return coins; }

    public void reset() {
        pipes.clear();
        coins.clear();
    }
}
