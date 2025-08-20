package com.example.speedtype.repository;

import com.example.speedtype.entity.TypingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypingResultRepository extends JpaRepository<TypingResult, Long> {

    List<TypingResult> findByUserUsernameOrderByDateDesc(String username);

    List<TypingResult> findByUserUsername(String username);

    List<TypingResult> findByUserIdOrderByDateAsc(Long userId);

    @Query("SELECT tr FROM TypingResult tr WHERE tr.user.username = :username ORDER BY tr.date DESC")
    List<TypingResult> findResultsByUsername(@Param("username") String username);

    @Query(value = """
        SELECT u.username, 
               MAX(tr.typing_speed) as best_wpm,
               ROUND(AVG(tr.typing_speed)) as avg_wpm,
               COUNT(tr.id) as total_tests,
               'All' as difficulty,
               'All' as language,
               MAX(tr.test_date) as last_test_date
        FROM users u 
        JOIN typing_results tr ON u.id = tr.user_id 
        GROUP BY u.username 
        ORDER BY best_wpm DESC 
        LIMIT :limit""", nativeQuery = true)
    List<Object[]> findTopUsersByWPM(@Param("limit") int limit);

    @Query(value = """
        SELECT u.username, 
               MAX(tr.typing_speed) as best_wpm,
               ROUND(AVG(tr.typing_speed)) as avg_wpm,
               COUNT(tr.id) as total_tests,
               tr.difficulty,
               'All' as language,
               MAX(tr.test_date) as last_test_date
        FROM users u 
        JOIN typing_results tr ON u.id = tr.user_id 
        WHERE tr.difficulty = :difficulty
        GROUP BY u.username, tr.difficulty 
        ORDER BY best_wpm DESC 
        LIMIT :limit""", nativeQuery = true)
    List<Object[]> findTopUsersByDifficultyAndWPM(@Param("difficulty") String difficulty, @Param("limit") int limit);

    @Query(value = """
        SELECT u.username, 
               MAX(tr.typing_speed) as best_wpm,
               ROUND(AVG(tr.typing_speed)) as avg_wpm,
               COUNT(tr.id) as total_tests,
               'All' as difficulty,
               tr.language,
               MAX(tr.test_date) as last_test_date
        FROM users u 
        JOIN typing_results tr ON u.id = tr.user_id 
        WHERE tr.language = :language
        GROUP BY u.username, tr.language 
        ORDER BY best_wpm DESC 
        LIMIT :limit""", nativeQuery = true)
    List<Object[]> findTopUsersByLanguageAndWPM(@Param("language") String language, @Param("limit") int limit);
}