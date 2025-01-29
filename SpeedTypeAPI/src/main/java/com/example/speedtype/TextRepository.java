package com.example.speedtype;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TextRepository extends JpaRepository<Text, Long> {
    List<Text> findByDifficulty(String difficulty);
}