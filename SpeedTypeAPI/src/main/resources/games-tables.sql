-- Создание только новых таблиц для игр в реальном времени
-- Существующие таблицы users, texts, typing_results не затрагиваются

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
