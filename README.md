# 🎮 Java Battle Arena

> **실시간 수업 · 코딩 연습 · AI 튜터 · 운영 대시보드**  
> 국비 학원 / 자바 학습용 풀스택 교육 플랫폼

![Java](https://img.shields.io/badge/Java-17+-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![Build](https://img.shields.io/badge/Build-Maven-blue)
![DB](https://img.shields.io/badge/DB-PostgreSQL-4169E1)
![View](https://img.shields.io/badge/View-Thymeleaf-orange)
![License](https://img.shields.io/badge/License-MIT-black)

---

## ✨ 주요 기능

- **학생 모드**
  - 오늘의 학습: 강사 공유 자료, 퀴즈, 과제
  - 코딩 연습: 150+ 문제, 카테고리별 난이도, 제출 기록 저장
  - AI 튜터: Java 관련 질의응답
  - 복습 노트: 학습 노트 & 개인 기록 관리

- **강사 모드**
  - 실시간 모니터링 (WebSocket 기반)
  - 힌트 전송, 즉석 퀴즈, 자료 공유
  - 출석/진도 관리

- **관리자 모드**
  - 사용자 & 권한 관리
  - 학습 통계, 시스템 설정

---

## 🗂 프로젝트 구조
```text
src/main/java/com/javabattle/arena
├─ config/        # Security, CORS, WebSocket, JWT
├─ model/         # User, Problem, Submission, Note, Quiz, Stats ...
├─ repository/    # Spring Data JPA repositories
├─ service/       # User/Problem/Session/Jwt/AI Tutor services
├─ web/           # Controllers (Auth/Practice/Study/Teacher/Admin/WS)
└─ JavaBattleArenaApplication.java

src/main/resources
├─ templates/     # index, practice, solve, study, teacher, admin, ai-tutor, arena
├─ static/        # js, css, images
└─ application.yml   # (환경변수 기반 설정 권장) 

---

## 🧰 Tech Stack

- **Backend**: Spring Boot 3.2, Spring Security, Spring WebSocket, Spring Data JPA  
- **Database**: PostgreSQL  
- **View Layer**: Thymeleaf, Monaco Editor  
- **Authentication**: JWT (HS256)  
- **Build/Deploy**: Maven, Heroku  
- **AI**: Claude Messages API (선택)

---

## 🚀 실행 방법 (로컬 개발)

```bash
# 0) JDK 17+ / Maven 설치

# 1) 클론
git clone https://github.com/dakyumlee/java-battle-arena.git
cd java-battle-arena

# 2) DB 준비 (PostgreSQL)
#    create database arena; 등으로 로컬 DB 생성

# 3) 환경변수 설정
export JWT_SECRET=<your_secret>
export CLAUDE_API_KEY=<optional_ai_key>
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/arena
export SPRING_DATASOURCE_USERNAME=arena
export SPRING_DATASOURCE_PASSWORD=arena

# 4) 실행
mvn spring-boot:run


⸻

🕹 향후 로드맵 (Coding Battle)
	•	매치메이킹 API (/api/battle/match)
	•	실시간 룸 동기화 (STOMP, /topic/room/{roomId})
	•	채점 파이프라인 (샌드박스 컨테이너, JUnit 테스트)
	•	점수판 & 랭킹 시스템
	•	관전 모드 & 리플레이

⸻

📈 Observability
	•	Spring Actuator /actuator/health
	•	메트릭/로그: Prometheus + Grafana (또는 CloudWatch)
	•	제출 기록 페이지네이션 & 통계 캐싱

⸻

🤝 Contributing
	•	버그 리포트, 기능 요청, PR 모두 환영합니다 🎉
	•	Issue 템플릿 사용 권장

