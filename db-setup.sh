echo "DB 최종 통합 설정!"

# 1. build.gradle에 DB 드라이버 추가
cat >> build.gradle << 'EOF'

    // Database Drivers
    runtimeOnly 'org.postgresql:postgresql'      // 운영환경
    runtimeOnly 'com.oracle.database.jdbc:ojdbc11'  // 로컬환경
EOF

# 2. 로컬 환경 (Oracle)
cat > src/main/resources/application-local.yml << 'EOF'
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:XE
    username: javabattle
    password: password
    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    database-platform: org.hibernate.dialect.Oracle12cDialect
    defer-datasource-initialization: true
  sql:
    init:
      mode: always
      data-locations: classpath:data-oracle.sql
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
EOF

# 3. 운영 환경 (PostgreSQL)  
cat > src/main/resources/application-prod.yml << 'EOF'
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/javabattle}
    username: ${DB_USERNAME:javabattle}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    database-platform: org.hibernate.dialect.PostgreSQLDialect
  sql:
    init:
      mode: never
logging:
  level:
    com.javabattle: INFO
    org.hibernate: WARN
EOF

# 4. PostgreSQL DDL (운영용)
cat > src/main/resources/db/migration/V001__init_schema.sql << 'EOF'
-- ENUM 타입들
CREATE TYPE challenge_type AS ENUM ('CODE','BUGFIX','QUERY','MAPPING');
CREATE TYPE submission_status AS ENUM ('PASS','FAIL','PARTIAL');
CREATE TYPE match_difficulty AS ENUM ('EASY','NORMAL','HARD');

-- players 테이블
CREATE TABLE players (
  id BIGSERIAL PRIMARY KEY,
  nickname VARCHAR(40) NOT NULL UNIQUE,
  exp INT NOT NULL DEFAULT 0,
  rank_tag VARCHAR(24) NOT NULL DEFAULT 'NOVICE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- rounds 테이블
CREATE TABLE rounds (
  id BIGSERIAL PRIMARY KEY,
  ord INT NOT NULL UNIQUE,
  title VARCHAR(100) NOT NULL,
  boss BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- challenges 테이블
CREATE TABLE challenges (
  id BIGSERIAL PRIMARY KEY,
  round_id BIGINT NOT NULL REFERENCES rounds(id) ON DELETE CASCADE,
  type challenge_type NOT NULL,
  title VARCHAR(120) NOT NULL,
  spec JSONB NOT NULL,
  expected JSONB,
  tests JSONB NOT NULL,
  ord INT NOT NULL DEFAULT 1,
  UNIQUE (round_id, ord)
);

-- submissions 테이블
CREATE TABLE submissions (
  id BIGSERIAL PRIMARY KEY,
  player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
  challenge_id BIGINT NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
  score NUMERIC(5,2) NOT NULL DEFAULT 0,
  status submission_status NOT NULL,
  stdout TEXT,
  stderr TEXT,
  elapsed_ms INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- badges 테이블
CREATE TABLE badges (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(60) NOT NULL,
  description VARCHAR(200) NOT NULL
);

-- player_badges 조인 테이블
CREATE TABLE player_badges (
  id BIGSERIAL PRIMARY KEY,
  player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
  badge_id BIGINT NOT NULL REFERENCES badges(id) ON DELETE CASCADE,
  granted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (player_id, badge_id)
);

-- matches 테이블
CREATE TABLE matches (
  id BIGSERIAL PRIMARY KEY,
  player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
  mode VARCHAR(20) NOT NULL,
  difficulty match_difficulty NOT NULL DEFAULT 'NORMAL',
  started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  cleared_at TIMESTAMPTZ,
  cleared BOOLEAN NOT NULL DEFAULT FALSE,
  total_score INT NOT NULL DEFAULT 0,
  combo_max INT NOT NULL DEFAULT 0,
  rounds_cleared INT NOT NULL DEFAULT 0
);

-- 인덱스들
CREATE INDEX idx_challenges_round ON challenges(round_id);
CREATE INDEX idx_submissions_player ON submissions(player_id);
CREATE INDEX idx_submissions_challenge ON submissions(challenge_id);
CREATE INDEX idx_submissions_created ON submissions(created_at DESC);
CREATE INDEX idx_player_badges_player ON player_badges(player_id);
CREATE INDEX idx_matches_rank ON matches (cleared DESC, cleared_at, total_score DESC, combo_max DESC);
EOF

# 5. PostgreSQL 시드 데이터
cat > src/main/resources/db/migration/V002__seed_data.sql << 'EOF'
-- 라운드 데이터
INSERT INTO rounds (ord, title, boss) VALUES
(1, '문법 러시', false),
(6, 'JPA 성문', false),
(7, '연관관계 던전', false),
(10, '트랜잭션 성채', true);

-- 배지 데이터
INSERT INTO badges (code, name, description) VALUES
('ENTITY_MASTER', '엔티티 장인', 'R6 클리어'),
('NPLUS1_SLAYER', 'N+1 학살자', 'R7 성능최적화'),
('TX_DRAGON_SLAYER', '드래곤 슬레이어', '보스 격파');

-- 테스트 플레이어
INSERT INTO players (nickname, exp, rank_tag) VALUES
('테스터, 150, 'BRONZE'),
('자바마스터', 800, 'GOLD');

-- 샘플 문제들
INSERT INTO challenges (round_id, type, title, spec, tests, ord) VALUES
(1, 'CODE', 'FizzBuzz 자바버전', 
 '{"description": "1부터 n까지 3의배수는 Fizz, 5의배수는 Buzz"}',
 '{"cases": [{"input": 15, "output": "FizzBuzz"}]}', 1),

(6, 'MAPPING', 'Member 엔티티',
 '{"description": "Member 엔티티를 JPA 어노테이션으로 매핑"}',
 '{"validations": ["@Entity", "@Id"]}', 1),

(7, 'MAPPING', 'Post-Member 다대일',
 '{"description": "Post에서 Member로의 다대일 관계 매핑"}',
 '{"required": ["@ManyToOne", "@JoinColumn"]}', 1);
EOF

# 6. Oracle 시드 데이터 (로컬용)
cat > src/main/resources/data-oracle.sql << 'EOF'
-- 라운드 데이터
INSERT INTO rounds (ord, title, boss) VALUES (1, '문법 러시', 0);
INSERT INTO rounds (ord, title, boss) VALUES (6, 'JPA 성문', 0);
INSERT INTO rounds (ord, title, boss) VALUES (7, '연관관계 던전', 0);
INSERT INTO rounds (ord, title, boss) VALUES (10, '트랜잭션 성채', 1);

-- 배지 데이터
INSERT INTO badges (code, name, description) VALUES ('ENTITY_MASTER', '엔티티 장인', 'R6 클리어');
INSERT INTO badges (code, name, description) VALUES ('NPLUS1_SLAYER', 'N+1 학살자', 'R7 성능최적화');
INSERT INTO badges (code, name, description) VALUES ('TX_DRAGON_SLAYER', '드래곤 슬레이어', '보스 격파');

-- 테스트 플레이어
INSERT INTO players (nickname, exp, rank_tag) VALUES ('테스터, 150, 'BRONZE');
INSERT INTO players (nickname, exp, rank_tag) VALUES ('자바마스터', 800, 'GOLD');

-- 샘플 문제들
INSERT INTO challenges (round_id, type, title, spec, tests, ord) VALUES
(1, 'CODE', 'FizzBuzz', '{"desc": "3배수 Fizz"}', '{"test": "basic"}', 1);
INSERT INTO challenges (round_id, type, title, spec, tests, ord) VALUES  
(6, 'MAPPING', 'Member 엔티티', '{"desc": "JPA 매핑"}', '{"check": "@Entity"}', 1);

COMMIT;
EOF

# 7. 실행 스크립트들
cat > run-local.sh << 'EOF'
#!/bin/bash
echo "로컬 실행 (Oracle)"
./gradlew bootRun --args='--spring.profiles.active=local'
EOF

cat > run-prod.sh << 'EOF'
#!/bin/bash  
echo "운영 실행 (PostgreSQL)"
./gradlew bootRun --args='--spring.profiles.active=prod'
EOF

chmod +x run-local.sh run-prod.sh

echo ""
echo "DB 최종 통합 설정 완료!"
echo ""
echo "사용법:"
echo "로컬(Oracle):   ./run-local.sh"
echo "운영(PostgreSQL): ./run-prod.sh"
echo ""
echo "DB 준비:"
echo "Oracle:     CREATE USER javabattle IDENTIFIED BY password;"
echo "            GRANT CONNECT, RESOURCE TO javabattle;"
echo "PostgreSQL: CREATE DATABASE javabattle;"
