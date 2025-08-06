package com.example.speedtype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypingResultRepository extends JpaRepository<TypingResult, Long> {

    // Поиск по связанному пользователю
    List<TypingResult> findByUserUsernameOrderByDateDesc(String username);

    List<TypingResult> findByUserUsername(String username);

    // Альтернативный запрос через JPQL
    @Query("SELECT tr FROM TypingResult tr WHERE tr.user.username = :username ORDER BY tr.date DESC")
    List<TypingResult> findResultsByUsername(@Param("username") String username);
}
