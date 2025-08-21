-- для инициализации базы данных SpeedType

-- Создание таблиц для игр в реальном времени
CREATE TABLE IF NOT EXISTS games (
    id BIGSERIAL PRIMARY KEY,
    game_code VARCHAR(10) UNIQUE NOT NULL,
    text_id BIGINT REFERENCES texts(id),
    player1_id BIGINT REFERENCES users(id),
    player2_id BIGINT REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING_FOR_PLAYER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    winner_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS game_progress (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT REFERENCES games(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id),
    current_position INTEGER DEFAULT 0,
    wpm DOUBLE PRECISION DEFAULT 0.0,
    accuracy DOUBLE PRECISION DEFAULT 100.0,
    is_finished BOOLEAN DEFAULT FALSE,
    finish_time TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_games_code ON games(game_code);
CREATE INDEX IF NOT EXISTS idx_games_status ON games(status);
CREATE INDEX IF NOT EXISTS idx_game_progress_game_user ON game_progress(game_id, user_id);

INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$urDqYlq7/bHzFV/0k5D07u06jBUntSPCBptKW7D.r0ELUKkbONGKS', 'ADMIN'),
('testuser', '$2a$10$urDqYlq7/bHzFV/0k5D07u06jBUntSPCBptKW7D.r0ELUKkbONGKS', 'USER')
ON CONFLICT (username) DO NOTHING;

INSERT INTO texts (content, difficulty, language) VALUES
('The quick brown fox jumps over the lazy dog. This is a simple test.', 'easy', 'en'),
('Programming requires logical thinking and attention to detail.', 'easy', 'en'),
('Learning to type efficiently improves productivity and reduces fatigue.', 'easy', 'en'),
('Advanced algorithms and data structures are fundamental concepts in computer science.', 'medium', 'en'),
('Implementing efficient sorting algorithms such as quicksort and mergesort requires understanding of recursion.', 'medium', 'en'),
('Object-oriented programming principles include encapsulation, inheritance, and polymorphism.', 'medium', 'en'),
('Quantum computing represents a paradigm shift in computational methodology, utilizing quantum mechanical phenomena.', 'hard', 'en'),
('Cryptographic protocols ensure secure communication through mathematical complexity and computational infeasibility.', 'hard', 'en'),
('Machine learning algorithms require extensive training on large datasets to achieve optimal performance metrics.', 'hard', 'en'),

('Быстрая коричневая лиса прыгает через ленивую собаку. Это простой тест печати.', 'easy', 'ru'),
('Программирование требует логического мышления и внимания к деталям.', 'easy', 'ru'),
('Изучение эффективной печати повышает продуктивность и снижает усталость.', 'easy', 'ru'),
('Продвинутые алгоритмы и структуры данных являются фундаментальными концепциями информатики.', 'medium', 'ru'),
('Реализация эффективных алгоритмов сортировки требует понимания принципов рекурсии.', 'medium', 'ru'),
('Принципы объектно-ориентированного программирования включают инкапсуляцию, наследование и полиморфизм.', 'medium', 'ru'),
('Квантовые вычисления представляют смену парадигмы в вычислительной методологии, используя квантовые механические явления.', 'hard', 'ru'),
('Криптографические протоколы обеспечивают безопасную связь через математическую сложность и вычислительную невозможность.', 'hard', 'ru'),
('Алгоритмы машинного обучения требуют обширного обучения на больших наборах данных для достижения оптимальных показателей производительности.', 'hard', 'ru')
ON CONFLICT (content) DO NOTHING;
