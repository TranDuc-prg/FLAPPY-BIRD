package com.example.flap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements GameView.GameListener {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String gameMode = getIntent().getStringExtra(MainMenuActivity.EXTRA_GAME_MODE);
        int selectedBird1 = getIntent().getIntExtra(MainMenuActivity.EXTRA_SELECTED_BIRD1, R.drawable.bird1);
        int selectedBird2 = getIntent().getIntExtra(MainMenuActivity.EXTRA_SELECTED_BIRD2, R.drawable.bird13);

        gameView = new GameView(this);
        gameView.setGameMode(gameMode);
        gameView.setSelectedBirds(selectedBird1, selectedBird2);
        gameView.setGameListener(this);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.resumeGame();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit the game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ✅ GameListener: truyền cả score + level
    @Override
    public void onGameOver(int finalScore, int finalLevel) {
        runOnUiThread(() -> {
            showSaveScoreDialog(finalScore, finalLevel);
        });
    }

    @Override
    public void onToastRequested(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    // ✅ Dialog hiển thị cả Score + Level
    private void showSaveScoreDialog(int score, int level) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_score, null);
        final EditText input = dialogView.findViewById(R.id.et_player_name);

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Enter your name to save your score of " + score + " (Level " + level + "):")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "Player";
                    saveScore(name, score, level);
                    goToLeaderboard();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    goToLeaderboard();
                })
                .show();
    }

    // ✅ Lưu cả Score + Level
    private void saveScore(String name, int score, int level) {
        SharedPreferences prefs = getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String oldScores = prefs.getString("scores", "");
        String newEntry = name + ":" + score + ":" + level; // format: name:score:level
        String updatedScores = oldScores.isEmpty() ? newEntry : oldScores + "," + newEntry;

        editor.putString("scores", updatedScores);
        editor.apply();

        Toast.makeText(this, "Score & Level saved!", Toast.LENGTH_SHORT).show();
    }

    private void goToLeaderboard() {
        Intent intent = new Intent(GameActivity.this, LeaderboardActivity.class);
        startActivity(intent);
        finish();
    }
}
