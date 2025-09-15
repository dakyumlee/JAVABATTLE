# Java Battle Arena

실시간 수업 · 코딩 연습 · AI 튜터 · 운영 대시보드
자바 학습용 풀스택 교육 플랫폼

## 주요 기능
- 학생: 오늘의 학습 코딩 연습 AI 튜터 복습 노트
- 강사: 실시간 모니터링 힌트 전송 퀴즈 자료 공유
- 관리자: 사용자/권한 관리 학습 통계 시스템 설정

## 프로젝트 구조
src/main/java/com/javabattle/arena
- config  Security CORS WebSocket JWT
- model   User Problem Submission Note Quiz Stats
- repository  Spring Data JPA
- service User Problem Session Jwt AI Tutor
- web     Auth Practice Study Teacher Admin WS
- JavaBattleArenaApplication.java

src/main/resources
- templates  index practice solve study teacher admin ai-tutor arena
- static     js css images
- application.yml  (환경변수 기반 권장)

## Tech Stack
- Backend  Spring Boot 3.2 Security WebSocket Data JPA
- Database PostgreSQL
- View     Thymeleaf Monaco Editor
- Auth     JWT(HS256)
- Build    Maven
- Deploy   Heroku
- AI       Claude Messages API(선택)

## 실행 방법 (로컬)
# JDK 17+ / Maven 설치
# 로컬 DB 준비 후 환경변수로 연결
mvn spring-boot:run

## 로드맵 (Coding Battle)
- 매치메이킹 API (/api/battle/match)
- 실시간 룸 동기화 (STOMP /topic/room/{roomId})
- 채점 파이프라인 (샌드박스 컨테이너 JUnit)
- 점수판/랭킹 관전/리플레이

## Observability
- Actuator /actuator/health
- 로그/메트릭 수집, 제출 페이지네이션, 통계 캐싱

## License
MIT


⸻

2) 완전 민무늬 초안정판(폰에서 절대 안깨지게 최소 문법)

Java Battle Arena v2
====================

실시간 수업 코딩 연습 AI 튜터 운영 대시보드
자바 학습용 풀스택 교육 플랫폼

[주요 기능]
- 학생: 오늘의 학습 코딩 연습 AI 튜터 복습 노트
- 강사: 실시간 모니터링 힌트 전송 퀴즈 자료 공유
- 관리자: 사용자 권한 관리 학습 통계 시스템 설정

[프로젝트 구조]
src/main/java/com/javabattle/arena
 config / model / repository / service / web / Application
src/main/resources
 templates / static / application.yml

[Tech Stack]
Spring Boot 3.2, Security, WebSocket, JPA
PostgreSQL, Thymeleaf, Monaco Editor
JWT(HS256), Maven, Heroku, (옵션) Claude API

[실행 방법]
1) JDK 17+ Maven 설치
2) 로컬 PostgreSQL 생성 후 환경변수 설정
3) mvn spring-boot:run

[로드맵 - Coding Battle]
- /api/battle/match
- /topic/room/{roomId} STOMP
- 샌드박스 채점 JUnit
- 점수판 랭킹 관전 리플레이

[Observability]
- /actuator/health
- 로그/메트릭 수집 캐싱/페이지네이션

License: MIT


⸻