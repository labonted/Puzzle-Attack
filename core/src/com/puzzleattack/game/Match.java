package com.puzzleattack.game;

public class Match {
    private int count, multiplier;
    private int x;
    private int y;
    private float timer;

    public Match(int matchCount, int X, int Y, int TIMER, int multi){
        count = matchCount;
        multiplier = multi;
        x = X;
        y = Y;
        timer = TIMER;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
