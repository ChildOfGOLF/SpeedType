package com.example.speedtype.dto;

import com.example.speedtype.entity.TypingResult;

import java.time.LocalDateTime;

public class TypingResultResponseDTO {
    private Long id;
    private String userName;
    private int typingSpeed;
    private int errors;
    private String difficulty;
    private String language;
    private LocalDateTime date;

    public TypingResultResponseDTO() {}

    public TypingResultResponseDTO(TypingResult result) {
        this.id = result.getId();
        this.userName = result.getUserName();
        this.typingSpeed = result.getTypingSpeed();
        this.errors = result.getErrors();
        this.difficulty = result.getDifficulty();
        this.language = result.getLanguage();
        this.date = result.getDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTypingSpeed() {
        return typingSpeed;
    }

    public void setTypingSpeed(int typingSpeed) {
        this.typingSpeed = typingSpeed;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
