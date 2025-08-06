package com.example.speedtype;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "typing_results")
public class TypingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь многие-к-одному с пользователем
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "typing_speed", nullable = false)
    private int typingSpeed; // WPM

    @Column(name = "errors", nullable = false)
    private int errors;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "test_date", nullable = false)
    private LocalDateTime date;

    public TypingResult() {}

    public TypingResult(User user, int typingSpeed, int errors, String difficulty) {
        this.user = user;
        this.typingSpeed = typingSpeed;
        this.errors = errors;
        this.difficulty = difficulty;
        this.date = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    // Вспомогательный метод для получения имени пользователя
    public String getUserName() {
        return user != null ? user.getUsername() : null;
    }
}
