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
