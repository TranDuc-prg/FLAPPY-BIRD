package com.example.flap;

public class Level {
    private String key;
    private int levelId;
    private int pipeSpeed;
    private int pipeGap;

    // Default constructor for Firebase
    public Level() {
    }

    // Constructor with 3 parameters
    public Level(int levelId, int pipeSpeed, int pipeGap) {
        this.levelId = levelId;
        this.pipeSpeed = pipeSpeed;
        this.pipeGap = pipeGap;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLevelId() {
        return levelId;
    }

    public int getPipeSpeed() {
        return pipeSpeed;
    }

    public int getPipeGap() {
        return pipeGap;
    }
}