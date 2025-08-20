package com.example.speedtype.dto;

public class PlayerProgressUpdateDTO {
    private String username;
    private int currentPosition;
    private double wpm;
    private double accuracy;

    public PlayerProgressUpdateDTO() {}

    public PlayerProgressUpdateDTO(String username, int currentPosition, double wpm, double accuracy) {
        this.username = username;
        this.currentPosition = currentPosition;
        this.wpm = wpm;
        this.accuracy = accuracy;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(int currentPosition) { this.currentPosition = currentPosition; }

    public double getWpm() { return wpm; }
    public void setWpm(double wpm) { this.wpm = wpm; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
}
