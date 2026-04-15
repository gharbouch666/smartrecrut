# SmartRecrute

Smart Recruitment Platform with tag-based matching.

## Tech Stack
- Backend: Spring Boot 3.5.12, Java 17, PostgreSQL, JWT
- Frontend: Angular, Tailwind CSS

## Setup

### Backend
```bash
./mvnw clean install
./mvnw spring-boot:run
```
- Port: 8000
- DB: PostgreSQL `smartrecruit_db` (postgres/root)
- Swagger: http://localhost:8000/swagger-ui.html

### Frontend
```bash
cd frontend
npm install
ng serve
```
- Port: 4200

## API Endpoints
- Auth: POST /api/auth/login, /register
- Candidates: /api/candidats
- Jobs: /api/offres
- Applications: /api/candidatures
- Tags: /api/tags

## Features
- User registration/login (Admin/Recruiter/Candidate)
- Job posting & applications
- Tag matching & scoring
- Dashboard & Kanban statuses

