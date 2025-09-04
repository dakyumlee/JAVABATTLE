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
