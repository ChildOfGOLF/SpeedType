package com.example.speedtype.repository;

import com.example.speedtype.entity.Game;
import com.example.speedtype.entity.GameProgress;
import com.example.speedtype.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameProgressRepository extends JpaRepository<GameProgress, Long> {
    List<GameProgress> findByGameOrderByLastUpdatedDesc(Game game);
    Optional<GameProgress> findByGameAndUser(Game game, User user);
}
