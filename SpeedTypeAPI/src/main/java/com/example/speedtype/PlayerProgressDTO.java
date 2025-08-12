package com.example.speedtype;

public class PlayerProgressDTO {
    private String username;
    private int currentPosition;
    private double wpm;
    private double accuracy;
    private boolean isFinished;
    private String finishTime;

    public PlayerProgressDTO() {}

    public PlayerProgressDTO(User user, GameProgress progress) {
        this.username = user.getUsername();
        if (progress != null) {
            this.currentPosition = progress.getCurrentPosition();
            this.wpm = progress.getWpm();
            this.accuracy = progress.getAccuracy();
            this.isFinished = progress.getIsFinished();
            this.finishTime = progress.getFinishTime() != null ?
                progress.getFinishTime().toString() : null;
        }
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(int currentPosition) { this.currentPosition = currentPosition; }

    public double getWpm() { return wpm; }
    public void setWpm(double wpm) { this.wpm = wpm; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public boolean isFinished() { return isFinished; }
    public void setFinished(boolean finished) { isFinished = finished; }

    public String getFinishTime() { return finishTime; }
    public void setFinishTime(String finishTime) { this.finishTime = finishTime; }
}
