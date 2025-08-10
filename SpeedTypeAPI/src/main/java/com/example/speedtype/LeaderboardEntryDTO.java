package com.example.speedtype;

import java.time.LocalDateTime;

public class LeaderboardEntryDTO {
    private String username;
    private int bestWPM;
    private int averageWPM;
    private int totalTests;
    private String difficulty;
    private String language;
    private LocalDateTime lastTestDate;

    public LeaderboardEntryDTO() {}

    public LeaderboardEntryDTO(String username, int bestWPM, int averageWPM, int totalTests, String difficulty, String language, LocalDateTime lastTestDate) {
        this.username = username;
        this.bestWPM = bestWPM;
        this.averageWPM = averageWPM;
        this.totalTests = totalTests;
        this.difficulty = difficulty;
        this.language = language;
        this.lastTestDate = lastTestDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBestWPM() {
        return bestWPM;
    }

    public void setBestWPM(int bestWPM) {
        this.bestWPM = bestWPM;
    }

    public int getAverageWPM() {
        return averageWPM;
    }

    public void setAverageWPM(int averageWPM) {
        this.averageWPM = averageWPM;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getLastTestDate() {
        return lastTestDate;
    }

    public void setLastTestDate(LocalDateTime lastTestDate) {
        this.lastTestDate = lastTestDate;
    }
}
