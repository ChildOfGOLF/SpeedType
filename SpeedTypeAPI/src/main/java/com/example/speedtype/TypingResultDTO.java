package com.example.speedtype;

public class TypingResultDTO {
    private int typingSpeed;
    private int errors;
    private String difficulty;

    public TypingResultDTO() {}

    public TypingResultDTO(int typingSpeed, int errors, String difficulty) {
        this.typingSpeed = typingSpeed;
        this.errors = errors;
        this.difficulty = difficulty;
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
}
