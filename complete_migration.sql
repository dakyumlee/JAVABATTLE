-- 완전한 Java Battle Arena PostgreSQL 마이그레이션

-- 1. 사용자 테이블
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

-- 2. 활성 세션 테이블
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

-- 3. 문제 테이블
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

-- 4. 제출 테이블
CREATE TABLE submissions (
    submission_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    problem_id BIGINT REFERENCES problems(problem_id),
    code TEXT,
    status VARCHAR(20),
    score INTEGER,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 문제 제출 테이블
CREATE TABLE problem_submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    problem_title VARCHAR(500),
    answer TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score INTEGER,
    feedback VARCHAR(500)
);

-- 6. 퀴즈 테이블
CREATE TABLE quiz (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500),
    question TEXT,
    options TEXT,
    correct_answer INTEGER,
    schedule_type VARCHAR(20),
    schedule_time TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active INTEGER DEFAULT 1
);

-- 7. 퀴즈 제출 테이블
CREATE TABLE quiz_submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    quiz_title VARCHAR(500),
    question TEXT,
    user_answer INTEGER,
    correct_answer INTEGER,
    is_correct INTEGER,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. 학습 통계 테이블
CREATE TABLE learning_statistics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    total_study_time INTEGER DEFAULT 0,
    problems_solved INTEGER DEFAULT 0,
    problems_attempted INTEGER DEFAULT 0,
    quiz_correct INTEGER DEFAULT 0,
    quiz_total INTEGER DEFAULT 0,
    last_activity TIMESTAMP,
    streak_days INTEGER DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. AI 채팅 세션
CREATE TABLE ai_chat_sessions (
    session_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. AI 메시지
CREATE TABLE ai_messages (
    message_id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES ai_chat_sessions(session_id),
    user_message TEXT,
    ai_response TEXT,
    message_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. 학습 노트
CREATE TABLE study_notes (
    note_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    title VARCHAR(500),
    content TEXT,
    tags VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category VARCHAR(100),
    difficulty_level INTEGER,
    is_favorite INTEGER DEFAULT 0
);

-- 12. 사용자 진도
CREATE TABLE user_progress (
    progress_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    topic VARCHAR(255),
    skill_level INTEGER DEFAULT 1,
    last_studied TIMESTAMP,
    study_time_minutes INTEGER DEFAULT 0
);

-- 13. 카테고리
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    name VARCHAR(255),
    color VARCHAR(7),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. 선생님 힌트
CREATE TABLE teacher_hints (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    student_id BIGINT REFERENCES users(id),
    message TEXT,
    is_read INTEGER DEFAULT 0,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. 선생님 자료
CREATE TABLE teacher_materials (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    title VARCHAR(500),
    content TEXT,
    material_type VARCHAR(50),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    class_date DATE,
    youtube_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size INTEGER,
    file_path VARCHAR(500)
);

-- 16. 선생님 노트
CREATE TABLE teacher_notes (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    title VARCHAR(500),
    content TEXT,
    category VARCHAR(100),
    priority INTEGER DEFAULT 0,
    is_pinned INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 17. 빠른 문제
CREATE TABLE quick_problems (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    title VARCHAR(500),
    description TEXT,
    time_limit INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active INTEGER DEFAULT 1
);

-- 18. 퀴즈 문제들
CREATE TABLE quizzes (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    title VARCHAR(500),
    question TEXT,
    option1 VARCHAR(500),
    option2 VARCHAR(500),
    option3 VARCHAR(500),
    option4 VARCHAR(500),
    correct_answer INTEGER,
    time_limit INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active INTEGER DEFAULT 1
);

-- 인덱스 생성
CREATE INDEX idx_active_sessions_user ON active_sessions(user_id);
CREATE INDEX idx_submissions_user ON submissions(user_id);
CREATE INDEX idx_quiz_submissions_user ON quiz_submissions(user_id);
CREATE INDEX idx_learning_stats_user ON learning_statistics(user_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- 뷰 생성
CREATE VIEW v_student_monitor AS
SELECT 
    u.id as student_id,
    u.nickname,
    u.role,
    a.current_page,
    a.is_coding,
    a.code_length,
    a.last_activity,
    a.is_active,
    a.current_problem,
    CASE 
        WHEN a.is_coding = 1 THEN 'CODING'
        WHEN EXTRACT(EPOCH FROM (NOW() - a.last_activity))/60 < 5 THEN 'ACTIVE'
        WHEN EXTRACT(EPOCH FROM (NOW() - a.last_activity))/60 < 10 THEN 'IDLE'
        ELSE 'OFFLINE'
    END as activity_status,
    ROUND(EXTRACT(EPOCH FROM (NOW() - a.last_activity))/60) as minutes_since_activity
FROM users u
LEFT JOIN active_sessions a ON u.id = a.user_id
WHERE u.role = 'STUDENT';

CREATE VIEW v_student_monitor_simple AS
SELECT 
    u.id as student_id,
    u.nickname,
    u.role,
    a.current_page,
    a.is_coding,
    a.code_length,
    a.last_activity,
    a.is_active
FROM users u
LEFT JOIN active_sessions a ON u.id = a.user_id
WHERE u.role = 'STUDENT';

CREATE VIEW v_class_statistics AS
SELECT 
    COUNT(*) as total_students,
    COUNT(CASE WHEN a.is_active = 1 THEN 1 END) as active_students,
    COUNT(CASE WHEN a.is_coding = 1 THEN 1 END) as coding_students,
    ROUND(AVG(CASE WHEN a.code_length > 0 THEN a.code_length END)) as avg_code_length,
    MAX(a.last_activity) as latest_activity
FROM active_sessions a
JOIN users u ON a.user_id = u.id
WHERE u.role = 'STUDENT' 
AND a.is_active = 1;

-- 샘플 데이터 추가
-- 관리자 계정 생성 (비밀번호: admin123)
INSERT INTO users (email, password, nickname, role, level_points, experience) 
VALUES ('admin@javabattle.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5POv5dY3VlVjQs5yZxPww1BTWUOWXuC', 'Admin', 'TEACHER', 1000, 5000);

-- 샘플 문제 추가
INSERT INTO problems (title, description, difficulty, category, sample_input, sample_output, solution_template) 
VALUES (
    'Hello World',
    '화면에 "Hello, World!"를 출력하는 프로그램을 작성하세요.',
    'EASY',
    'Basic',
    '',
    'Hello, World!',
    'public class Solution {
    public static void main(String[] args) {
        // 여기에 코드를 작성하세요
    }
}'
);

INSERT INTO problems (title, description, difficulty, category, sample_input, sample_output, solution_template) 
VALUES (
    '두 수의 합',
    '두 정수 a와 b를 입력받아 a+b를 출력하는 프로그램을 작성하세요.',
    'EASY',
    'Basic',
    '3 5',
    '8',
    'import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 여기에 코드를 작성하세요
    }
}'
);
