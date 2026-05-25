package com.example.flap;

public class ScoreEntry {
    private String name;
    private int score;
    private int level;
    private String key; // Thêm trường này để lưu khóa từ Firebase

    public ScoreEntry() {
        // Constructor rỗng cho Firebase
    }

    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
        this.level = 1; // Mặc định level là 1
    }

    public ScoreEntry(String name, int score, int level) {
        this.name = name;
        this.score = score;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    // Thêm các phương thức getter và setter cho 'key'
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}