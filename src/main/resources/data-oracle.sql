-- 라운드 데이터 (Oracle은 다중 INSERT VALUES를 지원하지 않으므로 각각 분리)
INSERT INTO rounds (ord, title, boss, created_at) VALUES (1, '문법 러시', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (2, '조건문 마스터', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (3, '반복문 챌린지', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (4, '배열과 리스트', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (5, '객체지향 기초', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (6, 'JPA 엔티티', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (7, 'N+1 문제 해결', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (8, '복합 쿼리', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (9, '트랜잭션 마스터', 0, CURRENT_TIMESTAMP);
INSERT INTO rounds (ord, title, boss, created_at) VALUES (10, '최종 보스전', 1, CURRENT_TIMESTAMP);

-- 배지 데이터
INSERT INTO badges (code, name, description) VALUES ('SYNTAX_KILLER', '문법 학살자', '1라운드 클리어로 획득');
INSERT INTO badges (code, name, description) VALUES ('ENTITY_MASTER', '엔티티 장인', '6라운드 클리어로 획득');
INSERT INTO badges (code, name, description) VALUES ('NPLUS1_SLAYER', 'N+1 학살자', '7라운드 클리어로 획득');
INSERT INTO badges (code, name, description) VALUES ('TX_DRAGON_SLAYER', '드래곤 슬레이어', '10라운드 클리어로 획득');

-- 샘플 문제 (1라운드) - CLOB 타입이므로 간단한 문자열로
INSERT INTO challenges (round_id, type, title, spec, expected, tests, ord) VALUES 
(1, 'CODE', 'Hello World 출력하기', 
 '{"description": "Hello World를 출력하세요"}',
 '{"output": "Hello World"}',
 '{"testCases": [{"input": "", "expected": "Hello World"}]}',
 1);
