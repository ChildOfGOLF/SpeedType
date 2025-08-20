package com.example.speedtype.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_progress")
public class GameProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_position")
    private Integer currentPosition = 0;

    @Column(name = "wpm")
    private Double wpm = 0.0;

    @Column(name = "accuracy")
    private Double accuracy = 100.0;

    @Column(name = "is_finished")
    private Boolean isFinished = false;

    @Column(name = "finish_time")
    private LocalDateTime finishTime;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public GameProgress() {}

    public GameProgress(Game game, User user) {
        this.game = game;
        this.user = user;
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Integer currentPosition) { this.currentPosition = currentPosition; }

    public Double getWpm() { return wpm; }
    public void setWpm(Double wpm) { this.wpm = wpm; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Boolean getIsFinished() { return isFinished; }
    public void setIsFinished(Boolean isFinished) { this.isFinished = isFinished; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
