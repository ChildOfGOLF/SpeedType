package com.example.speedtype.repository;

import com.example.speedtype.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TextRepository extends JpaRepository<Text, Long> {
    List<Text> findByDifficulty(String difficulty);
    List<Text> findByDifficultyAndLanguage(String difficulty, String language);
    List<Text> findByLanguage(String language);
}