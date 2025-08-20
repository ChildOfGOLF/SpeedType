package com.example.speedtype.repository;

import com.example.speedtype.entity.Game;
import com.example.speedtype.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameCode(String gameCode);

    @Query("SELECT g FROM Game g WHERE g.status = 'WAITING_FOR_PLAYER' ORDER BY g.createdAt ASC")
    List<Game> findWaitingGames();

    List<Game> findByPlayer1OrPlayer2OrderByCreatedAtDesc(User player1, User player2);

    @Query("SELECT g FROM Game g WHERE (g.player1 = ?1 OR g.player2 = ?1) AND g.status IN ('WAITING_FOR_PLAYER', 'READY_TO_START', 'IN_PROGRESS')")
    Optional<Game> findActiveGameByUser(User user);
}
