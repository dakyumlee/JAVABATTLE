-- Java Battle Arena PostgreSQL 마이그레이션

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'STUDENT',
    level_points INTEGER DEFAULT 0,
    experience INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active INTEGER DEFAULT 1
);

CREATE TABLE active_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    session_id VARCHAR(255),
    current_page VARCHAR(255),
    current_code TEXT,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_coding INTEGER DEFAULT 0,
    is_active INTEGER DEFAULT 1,
    current_problem VARCHAR(255),
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    code_length INTEGER DEFAULT 0
);

CREATE TABLE problems (
    problem_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500),
    description TEXT,
    difficulty VARCHAR(20),
    category VARCHAR(100),
    sample_input TEXT,
    sample_output TEXT,
    solution_template TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (email, password, nickname, role, level_points, experience) 
VALUES ('admin@javabattle.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5POv5dY3VlVjQs5yZxPww1BTWUOWXuC', 'Admin', 'TEACHER', 1000, 5000);
