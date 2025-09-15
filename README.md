# 🎮 Java Battle Arena v2

**실시간 수업 · 코딩 연습 · AI 튜터 · 운영 대시보드**  
자바 학습용 풀스택 교육 플랫폼

---

## ✨ 주요 기능
- 👩‍🎓 **학생**  
  오늘의 학습 · 코딩 연습 · AI 튜터 · 복습 노트  
- 👨‍🏫 **강사**  
  실시간 모니터링 · 힌트 전송 · 퀴즈 · 자료 공유  
- 🛠️ **관리자**  
  사용자/권한 관리 · 학습 통계 · 시스템 설정  

---

## 🗂 프로젝트 구조
```text
src/main/java/com/javabattle/arena
 ├─ config        # Security, CORS, WebSocket, JWT
 ├─ model         # User, Problem, Submission, Note, Quiz, Stats
 ├─ repository    # Spring Data JPA
 ├─ service       # User, Problem, Session, Jwt, AI Tutor
 ├─ web           # Auth, Practice, Study, Teacher, Admin, WS
 └─ JavaBattleArenaApplication.java

src/main/resources
 ├─ templates     # index, practice, solve, study, teacher, admin, ai-tutor, arena
 ├─ static        # js, css, images
 └─ application.yml   # (환경변수 기반 권장)


⸻

🧰 Tech Stack
	•	Backend: Spring Boot 3.2 · Security · WebSocket · Data JPA
	•	Database: PostgreSQL
	•	View: Thymeleaf · Monaco Editor
	•	Auth: JWT (HS256)
	•	Build: Maven
	•	Deploy: Heroku
	•	AI (옵션): Claude Messages API

⸻

🚀 실행 방법 (로컬)

# 0) JDK 17+ / Maven 설치

# 1) 클론
git clone https://github.com/dakyumlee/java-battle-arena.git
cd java-battle-arena

# 2) DB 준비 (PostgreSQL)
#    create database arena; 등으로 로컬 DB 생성

# 3) 환경변수 설정
export JWT_SECRET=<ㅎㅎ>
export CLAUDE_API_KEY=<ㅎㅎ>
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/arena
export SPRING_DATASOURCE_USERNAME=arena
export SPRING_DATASOURCE_PASSWORD=arena

# 4) 실행
mvn spring-boot:run


⸻

🕹 로드맵 (Coding Battle)
	•	매치메이킹 API (/api/battle/match)
	•	실시간 룸 동기화 (STOMP /topic/room/{roomId})
	•	채점 파이프라인 (샌드박스 컨테이너 · JUnit 테스트)
	•	점수판 & 랭킹 시스템
	•	관전 모드 & 리플레이

⸻

📈 Observability
	•	Spring Actuator /actuator/health
	•	로그/메트릭 수집
	•	제출 페이지네이션 & 통계 캐싱

