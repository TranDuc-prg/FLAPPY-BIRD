package com.example.flap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class GameView extends View {

    private Bird bird1, bird2;
    private PipeManager pipeManager;
    private int screenWidth, screenHeight;
    private Paint paint;
    private Handler handler;
    private boolean gameOver;
    private boolean isPaused = false;
    private Bitmap backgroundImage;

    private MediaPlayer flapSound, hitSound, coinSound;
    private MediaPlayer backgroundMusic;

    private int score1 = 0, score2 = 0;
    private boolean player1Dead = false, player2Dead = false;

    private SharedPreferences prefs;
    private int highScore;
    private boolean scoreSaved = false;

    private String gameMode = "single";
    private float gravityFactor = 1.0f;
    private int framesSinceStart = 0;

    private int selectedBirdResId1 = R.drawable.bird1;
    private int selectedBirdResId2 = R.drawable.bird13;

    private Bitmap pauseIcon, playIcon;
    private Rect pauseButtonRect;

    private Bitmap trophyIcon;
    private float trophyScale = 0f;

    private List<Particle> particles = new ArrayList<>();
    private Random rand = new Random();

    private List<GameObject> gameObjects = new ArrayList<>();

    // Level
    private int level = 1;

    public interface GameListener {
        void onGameOver(int finalScore, int finalLevel);
        void onToastRequested(String message);
    }

    private GameListener gameListener;

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    public GameView(Context context) { super(context); init(context); }
    public GameView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs); init(context); }

    private void init(Context context) {
        paint = new Paint();
        handler = new Handler();
        gameOver = false;

        prefs = context.getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("high_score", 0);
        level = prefs.getInt("last_level", 1);
        score1 = prefs.getInt("last_score", 0);  // 🔥 load điểm cũ

        flapSound = MediaPlayer.create(context, R.raw.flap_sound);
        hitSound = MediaPlayer.create(context, R.raw.hit_sound);
        coinSound = MediaPlayer.create(context, R.raw.coin_sound1);

        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f, 0.5f);
        }

        pipeManager = new PipeManager(context);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
                handler.postDelayed(this, 30);
            }
        }, 30);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setGameMode(String mode) {
        this.gameMode = mode;
        gravityFactor = mode.equals("gravity") ? 2.0f : 1.0f;
        if (screenHeight > 0) resetForMode();
    }

    public void setSelectedBirds(int birdResId1, int birdResId2) {
        this.selectedBirdResId1 = birdResId1;
        this.selectedBirdResId2 = birdResId2;
    }

    private void resetForMode() {
        int[] bird1Frames = getBirdFrames(selectedBirdResId1);
        int[] bird2Frames = getBirdFrames(selectedBirdResId2);

        gameObjects.clear();

        if (gameMode.equals("multiplayer")) {
            bird1 = new Bird(screenWidth / 3f, screenHeight / 2f, getContext(), bird1Frames, gravityFactor, screenHeight);
            bird2 = new Bird(screenWidth / 3f + 120, screenHeight / 2f, getContext(), bird2Frames, gravityFactor, screenHeight);
            gameObjects.add(bird1);
            gameObjects.add(bird2);
        } else {
            bird1 = new Bird(screenWidth / 2f - 25, screenHeight / 2f, getContext(), bird1Frames, gravityFactor, screenHeight);
            bird2 = null;
            gameObjects.add(bird1);
        }

        player1Dead = false;
        player2Dead = false;

        // 🔥 Load lại điểm đã lưu thay vì reset về 0
        score1 = prefs.getInt("last_score", 0);
        score2 = 0;

        gameOver = false;
        scoreSaved = false;
        framesSinceStart = 0;
        isPaused = false;
        trophyScale = 0f;
        particles.clear();

        pipeManager.reset();

        pipeManager.setPipeSpeed(10 + (level - 1) * 2);
        pipeManager.setPipeGap(450 - (level - 1) * 20);

        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.seekTo(0);
            backgroundMusic.start();
        }
    }

    private int[] getBirdFrames(int resId) {
        if (resId == R.drawable.bird15) return new int[]{R.drawable.bird14, R.drawable.bird15, R.drawable.bird16};
        if (resId == R.drawable.bird10) return new int[]{R.drawable.bird10, R.drawable.bird11, R.drawable.bird12};
        if (resId == R.drawable.bird12) return new int[]{R.drawable.bird10, R.drawable.bird11, R.drawable.bird12};
        if (resId == R.drawable.bird13) return new int[]{R.drawable.bird13, R.drawable.bird8, R.drawable.bird9};
        if (resId == R.drawable.bird23) return new int[]{R.drawable.bird21, R.drawable.bird22, R.drawable.bird23};
        if (resId == R.drawable.bird33) return new int[]{R.drawable.bird31, R.drawable.bird32, R.drawable.bird33};
        if (resId == R.drawable.bird43) return new int[]{R.drawable.bird41, R.drawable.bird42, R.drawable.bird43};
        if (resId == R.drawable.bird53) return new int[]{R.drawable.bird51, R.drawable.bird52, R.drawable.bird53};
        if (resId == R.drawable.bird63) return new int[]{R.drawable.bird61, R.drawable.bird61, R.drawable.bird63};
        return new int[]{resId};
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        backgroundImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background),
                screenWidth, screenHeight, false);

        pipeManager.setScreenHeight(screenHeight);
        pipeManager.setScreenWidth(screenWidth);

        pauseIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pause);
        playIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play);
        int btnSize = 120;
        pauseButtonRect = new Rect(30, screenHeight - btnSize - 30, 30 + btnSize, screenHeight - 30);

        trophyIcon = BitmapFactory.decodeResource(getResources(), R.drawable.trophy);

        resetForMode();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (backgroundImage != null) canvas.drawBitmap(backgroundImage, 0, 0, null);
        else canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        canvas.drawText("Score: " + Math.max(score1, score2), screenWidth / 2f - 150, 80, paint);
        canvas.drawText("Level: " + level, screenWidth / 2f - 150, 160, paint);

        if (gameMode.equals("multiplayer")) {
            canvas.drawText("P1: " + score1, 30, 80, paint);
            canvas.drawText("P2: " + score2, screenWidth - 250, 80, paint);
        }

        if (isPaused) canvas.drawBitmap(playIcon, null, pauseButtonRect, null);
        else canvas.drawBitmap(pauseIcon, null, pauseButtonRect, null);

        if (!gameOver && !isPaused) {
            framesSinceStart++;
            for (GameObject obj : gameObjects) {
                obj.update();
                obj.draw(canvas, paint);
            }
            pipeManager.update();
            pipeManager.draw(canvas, paint);
            if (framesSinceStart > 20) updateAndCheckCollision();
        } else if (isPaused) {
            paint.setColor(Color.CYAN);
            paint.setTextSize(100);
            canvas.drawText("PAUSED", screenWidth / 2f - 150, screenHeight / 2f, paint);
        } else {
            drawGameOver(canvas);
        }
    }

    private void updateAndCheckCollision() {
        if (bird1 != null && !player1Dead) bird1.update();
        if (bird2 != null && !player2Dead) bird2.update();

        Rect rect1 = bird1 != null ? bird1.getRect() : null;
        Rect rect2 = bird2 != null ? bird2.getRect() : null;

        if (bird1 != null && !player1Dead && bird1.getY() + bird1.getRect().height() >= screenHeight) {
            player1Dead = true; if (hitSound != null) hitSound.start();
        }
        if (bird2 != null && !player2Dead && bird2.getY() + bird2.getRect().height() >= screenHeight) {
            player2Dead = true; if (hitSound != null) hitSound.start();
        }

        for (Pipe pipe : pipeManager.getPipes()) {
            if (!player1Dead && rect1 != null && (Rect.intersects(rect1, pipe.getTopRect()) || Rect.intersects(rect1, pipe.getBottomRect()))) {
                player1Dead = true; if (hitSound != null) hitSound.start();
                break;
            }
            if (!player2Dead && rect2 != null && (Rect.intersects(rect2, pipe.getTopRect()) || Rect.intersects(rect2, pipe.getBottomRect()))) {
                player2Dead = true; if (hitSound != null) hitSound.start();
                break;
            }
        }

        for (Coin coin : pipeManager.getCoins()) {
            if (!coin.isCollected()) {
                if (!player1Dead && rect1 != null && Rect.intersects(rect1, coin.getRect())) {
                    coin.setCollected(true); score1 += 5;
                    if (coinSound != null) coinSound.start();
                } else if (!player2Dead && rect2 != null && Rect.intersects(rect2, coin.getRect())) {
                    coin.setCollected(true); score2 += 5;
                    if (coinSound != null) coinSound.start();
                }
            }
        }

        for (Pipe pipe : pipeManager.getPipes()) {
            if (!pipe.isScored()) {
                if (!player1Dead && bird1 != null && bird1.getX() > pipe.getX() + pipe.getWidth()) {
                    score1++; pipe.setScored(true); checkLevelUp();
                } else if (!player2Dead && bird2 != null && bird2.getX() > pipe.getX() + pipe.getWidth()) {
                    score2++; pipe.setScored(true); checkLevelUp();
                }
            }
        }

        if ((gameMode.equals("single") || gameMode.equals("gravity")) && player1Dead) {
            gameOver = true;
            stopBackgroundMusic();
        } else if (gameMode.equals("multiplayer") && player1Dead && player2Dead) {
            gameOver = true;
            stopBackgroundMusic();
        }
    }

    private void checkLevelUp() {
        int bestScoreNow = Math.max(score1, score2);

        if (bestScoreNow > 0 && bestScoreNow % 10 == 0) {
            int newLevel = (bestScoreNow / 10) + 1;
            if (newLevel > level) {
                level = newLevel;

                int newSpeed = 10 + (level - 1) * 2;
                int newGap = Math.max(200, 450 - (level - 1) * 20);

                pipeManager.setPipeSpeed(newSpeed);
                pipeManager.setPipeGap(newGap);

                prefs.edit().putInt("last_level", level).apply();

                if (gameListener != null) {
                    gameListener.onToastRequested("🔥 Level Up! Level " + level);
                }
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        paint.setTextSize(100);
        paint.setColor(Color.RED);
        String gameOverText = "Game Over";
        float textWidth = paint.measureText(gameOverText);
        float textX = screenWidth / 2f - textWidth / 2f;
        float textY = screenHeight / 2f;
        canvas.drawText(gameOverText, textX, textY, paint);

        paint.setTextSize(60);
        paint.setColor(Color.WHITE);
        int bestScoreNow = Math.max(score1, score2);
        canvas.drawText("Score: " + bestScoreNow, screenWidth / 2f - 150, screenHeight / 2f + 80, paint);
        canvas.drawText("Level: " + level, screenWidth / 2f - 150, screenHeight / 2f + 150, paint);
        canvas.drawText("High Score: " + highScore, screenWidth / 2f - 150, screenHeight / 2f + 220, paint);

        if (bestScoreNow > highScore) {
            highScore = bestScoreNow;
            prefs.edit().putInt("high_score", highScore).apply();
            if (gameListener != null) {
                gameListener.onToastRequested("🎉 High Score mới!");
            }
        }

        if (!scoreSaved) {
            scoreSaved = true;
            prefs.edit()
                    .putInt("last_level", level)
                    .putInt("last_score", bestScoreNow)
                    .apply();
            if (gameListener != null) {
                gameListener.onGameOver(bestScoreNow, level);
            }
        }

        vibrateOnGameOver();
    }

    private void vibrateOnGameOver() {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) v.vibrate(300);
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            if (backgroundMusic.isPlaying()) backgroundMusic.pause();
            backgroundMusic.seekTo(0);
        }
    }

    public void pauseGame() {
        isPaused = true;
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    public void resumeGame() {
        isPaused = false;
        if (backgroundMusic != null) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (flapSound != null) flapSound.release();
        if (hitSound != null) hitSound.release();
        if (coinSound != null) coinSound.release();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
        }
    }

    // ✅ Điều khiển chim bằng chạm màn hình
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!gameOver && !isPaused) {
                if (bird1 != null && !player1Dead) bird1.flap();
                if (gameMode.equals("multiplayer") && bird2 != null && !player2Dead) bird2.flap();
                if (flapSound != null) flapSound.start();
            } else if (gameOver) {
                resetForMode(); // 🔥 chơi tiếp với level & score cũ
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            if (!gameOver && !isPaused) {
                if (bird1 != null && !player1Dead) bird1.flap();
                if (gameMode.equals("multiplayer") && bird2 != null && !player2Dead) bird2.flap();
                if (flapSound != null) flapSound.start();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static class Particle {
        float x, y, vx, vy;
        int color, life;
        float size;
        Particle(float x, float y, float vx, float vy, int color, int life) {
            this.x=x; this.y=y; this.vx=vx; this.vy=vy;
            this.color=color; this.life=life;
            this.size=randSize();
        }
        private static float randSize() { return (float)(Math.random()*8+3); }
    }
}
