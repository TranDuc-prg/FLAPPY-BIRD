package com.example.flap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private ListView listView;
    private Button clearButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Initialize views
        listView = findViewById(R.id.scoresList);
        clearButton = findViewById(R.id.clearButton);
        backButton = findViewById(R.id.backButton);

        // Load and display leaderboard
        loadAndShowScores();

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Set up clear button to clear leaderboard data
        clearButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
            prefs.edit().remove("scores").apply();
            loadAndShowScores();
            Toast.makeText(LeaderboardActivity.this, "Leaderboard cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadAndShowScores() {
        SharedPreferences prefs = getSharedPreferences("FlappyPrefs", Context.MODE_PRIVATE);
        String raw = prefs.getString("scores", "");
        List<String> items = new ArrayList<>();

        if (raw == null || raw.trim().isEmpty()) {
            items.add("Chưa có điểm nào được lưu.");
        } else {
            String[] parts = raw.split(",");
            List<ScoreEntry> scoreEntries = new ArrayList<>();

            for (String part : parts) {
                String[] entry = part.split(":");
                // Kiểm tra xem chuỗi có đủ các phần cần thiết không (tên, điểm, level)
                if (entry.length >= 2) {
                    String name = entry[0].trim();
                    String scoreStr = entry[1].trim();
                    int level = 1;

                    // Lấy level từ chuỗi nếu có
                    if (entry.length > 2) {
                        try {
                            level = Integer.parseInt(entry[2].trim());
                        } catch (NumberFormatException e) {
                            Log.e("LeaderboardActivity", "Error parsing level: " + entry[2], e);
                        }
                    }

                    try {
                        int score = Integer.parseInt(scoreStr);
                        scoreEntries.add(new ScoreEntry(name, score, level));
                    } catch (NumberFormatException e) {
                        Log.e("LeaderboardActivity", "Error parsing score: " + scoreStr, e);
                    }
                }
            }

            if (scoreEntries.isEmpty()) {
                items.add("Không có điểm hợp lệ nào được lưu.");
            } else {
                // Sắp xếp theo điểm số giảm dần, nếu cùng điểm thì sắp xếp theo cấp độ giảm dần
                Collections.sort(scoreEntries, new Comparator<ScoreEntry>() {
                    @Override
                    public int compare(ScoreEntry o1, ScoreEntry o2) {
                        int scoreCompare = Integer.compare(o2.score, o1.score);
                        if (scoreCompare != 0) {
                            return scoreCompare;
                        }
                        return Integer.compare(o2.level, o1.level);
                    }
                });

                for (int i = 0; i < scoreEntries.size(); i++) {
                    ScoreEntry entry = scoreEntries.get(i);
                    items.add((i + 1) + ". " + entry.name + " - Level: " + entry.level + ", Score: " + entry.score);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                items
        );
        listView.setAdapter(adapter);
    }

    private static class ScoreEntry {
        String name;
        int score;
        int level;

        ScoreEntry(String name, int score, int level) {
            this.name = name;
            this.score = score;
            this.level = level;
        }
    }
}