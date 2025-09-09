#!/bin/bash

# 🚀 Java Battle Arena 완전 자동 배포 스크립트
# 실행: chmod +x deploy.sh && ./deploy.sh

echo "🔥 Java Battle Arena Heroku 배포 시작!"
echo "========================================"

# 앱 이름 생성 (유니크하게)
APP_NAME="java-battle-arena-$(date +%s)"
echo "📱 앱 이름: $APP_NAME"

#!/bin/bash

# 🚀 Java Battle Arena 완전 자동 배포 스크립트
# 실행: chmod +x deploy.sh && ./deploy.sh

echo "🔥 Java Battle Arena Heroku 배포 시작!"
echo "========================================"

# 앱 이름 생성 (유니크하게)
APP_NAME="java-battle-arena-$(date +%s)"
echo "📱 앱 이름: $APP_NAME"

# 1. Heroku 로그인 확인
echo "🔐 Heroku 로그인 확인 중..."
if ! heroku auth:whoami > /dev/null 2>&1; then
    echo "❌ Heroku에 로그인이 필요합니다."
    heroku login
fi

# 2. PostgreSQL 마이그레이션 SQL 파일 생성
echo "🗄️ PostgreSQL 마이그레이션 파일 생성 중..."
cat > migration.sql << 'EOF'
-- Java Battle Arena PostgreSQL 스키마

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

CREATE TABLE submissions (
    submission_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    problem_id BIGINT REFERENCES problems(problem_id),
    code TEXT,
    status VARCHAR(20),
    score INTEGER,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE problem_submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    problem_title VARCHAR(500),
    answer TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score INTEGER,
    feedback VARCHAR(500)
);

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

CREATE TABLE ai_chat_sessions (
    session_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_messages (
    message_id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES ai_chat_sessions(session_id),
    user_message TEXT,
    ai_response TEXT,
    message_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE user_progress (
    progress_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    topic VARCHAR(255),
    skill_level INTEGER DEFAULT 1,
    last_studied TIMESTAMP,
    study_time_minutes INTEGER DEFAULT 0
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    name VARCHAR(255),
    color VARCHAR(7),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teacher_hints (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    student_id BIGINT REFERENCES users(id),
    message TEXT,
    is_read INTEGER DEFAULT 0,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE quick_problems (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id),
    title VARCHAR(500),
    description TEXT,
    time_limit INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active INTEGER DEFAULT 1
);

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

EOF

echo "✅ migration.sql 파일 생성 완료!"

# 3. application-prod.yml 생성
echo "⚙️ 운영 설정 파일 생성 중..."
mkdir -p src/main/resources
cat > src/main/resources/application-prod.yml << 'EOF'
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        
  profiles:
    active: prod
    
server:
  port: ${PORT:8080}
  
logging:
  level:
    org.springframework.security: INFO
    com.javabattle: INFO
    
jwt:
  secret: ${JWT_SECRET:java-battle-arena-super-secret-jwt-key-for-production-must-be-very-long-and-secure}
  expiration: 86400000
EOF

# 4. build.gradle에 PostgreSQL 의존성 확인/추가
echo "📦 build.gradle PostgreSQL 의존성 확인 중..."
if ! grep -q "org.postgresql:postgresql" build.gradle; then
    echo "PostgreSQL 의존성을 build.gradle에 추가하는 중..."
    # Oracle 의존성을 주석처리하고 PostgreSQL 추가
    sed -i.bak 's/.*ojdbc.*/#&/' build.gradle
    sed -i.bak '/dependencies {/a\
    runtimeOnly '\''org.postgresql:postgresql'\''
' build.gradle
fi

# 5. Procfile 생성
echo "📄 Procfile 생성 중..."
cat > Procfile << 'EOF'
web: java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar build/libs/*.jar
EOF

# 6. system.properties 생성 (Java 버전 명시)
echo "☕ system.properties 생성 중..."
cat > system.properties << 'EOF'
java.runtime.version=17
EOF

# 7. Heroku 앱 생성
echo "🎯 Heroku 앱 생성 중: $APP_NAME"
heroku create $APP_NAME

# 8. PostgreSQL 애드온 추가
echo "🐘 PostgreSQL 데이터베이스 추가 중..."
heroku addons:create heroku-postgresql:essential-0 --app $APP_NAME

# 9. 환경변수 설정
echo "🔧 환경변수 설정 중..."
heroku config:set SPRING_PROFILES_ACTIVE=prod --app $APP_NAME
heroku config:set JWT_SECRET="java-battle-arena-super-secret-jwt-key-$(date +%s)" --app $APP_NAME

# 10. Git 초기화 및 커밋
echo "📝 Git 설정 중..."
if [ ! -d ".git" ]; then
    git init
fi

# Heroku remote 추가
heroku git:remote -a $APP_NAME

# Git 커밋
git add .
git commit -m "Deploy Java Battle Arena to Heroku with PostgreSQL"

# 11. 애플리케이션 배포
echo "🚀 애플리케이션 배포 중..."
git push heroku main

# 12. 데이터베이스 스키마 생성
echo "🗄️ 데이터베이스 스키마 생성 중..."
sleep 10  # 앱이 완전히 배포될 때까지 대기
heroku pg:psql --app $APP_NAME < migration.sql

# 13. 샘플 데이터 추가 (옵션)
echo "📊 샘플 데이터 추가 중..."
heroku pg:psql --app $APP_NAME << 'EOF'
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
    'public class Solution {\n    public static void main(String[] args) {\n        // 여기에 코드를 작성하세요\n    }\n}'
);

INSERT INTO problems (title, description, difficulty, category, sample_input, sample_output, solution_template) 
VALUES (
    '두 수의 합',
    '두 정수 a와 b를 입력받아 a+b를 출력하는 프로그램을 작성하세요.',
    'EASY',
    'Basic',
    '3 5',
    '8',
    'import java.util.Scanner;\n\npublic class Solution {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        // 여기에 코드를 작성하세요\n    }\n}'
);
EOF

# 14. 배포 완료
echo ""
echo "🎉 배포 완료!"
echo "========================================"
echo "📱 앱 이름: $APP_NAME"
echo "🌐 앱 URL: https://$APP_NAME.herokuapp.com"
echo "🗄️ 데이터베이스: PostgreSQL"
echo ""
echo "📋 관리자 계정:"
echo "   이메일: admin@javabattle.com"
echo "   비밀번호: admin123"
echo ""
echo "🔧 유용한 명령어들:"
echo "   heroku logs --tail --app $APP_NAME     # 로그 확인"
echo "   heroku pg:psql --app $APP_NAME         # DB 접속"
echo "   heroku restart --app $APP_NAME         # 앱 재시작"
echo ""

# 15. 브라우저에서 앱 열기
echo "🌐 브라우저에서 앱 열기..."
heroku open --app $APP_NAME

# 정리
rm migration.sql

echo "✅ 모든 배포 과정이 완료되었습니다!"

