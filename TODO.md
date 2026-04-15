# SmartRecrute Full Implementation TODO

Completed: 0/20

## SPRINT 1-2: Tags & Profiles
1. [x] Backend: Tag CRUD complete + seed 42 skills
2. [x] Frontend: Admin tags page ( /admin/tags )
3. [ ] Frontend: Candidate skills form (matrix + levels)
4. [ ] Backend: ProfilTag create/update for candidates
5. [ ] Backend: Recruteur role/register
6. [ ] Frontend: Job create with required/bonus tags
7. [ ] Backend: TagOffre for jobs

## SPRINT 3: Matching
8. [ ] Backend: Full scoring algorithm
9. [ ] Frontend: Scores display + filter

## SPRINT 4: Pipeline
10. [ ] Frontend: Kanban drag-drop
11. [ ] Backend: Status update API

## Later SPRINTs
12. [ ] Messaging
13. [ ] AI (OpenAI/Python)
14. [ ] Admin dashboard
... 

## Test
mvn test
npm test

## Steps from Approved Plan

1. [x] Fix `cryptpassword.java` -> `CryptPasswordConfig.java` (rename file/class).
2. [x] Fix `securityconfig.java` -> `SecurityConfig.java` (rename file/class, update imports).
3. [x] Update pom.xml - add Swagger deps.
4. [x] Create SwaggerConfig.java.
5. [x] Create/enhance README.md.
6. [x] Minor AuthService cleanup (TODO annotations).
7. [x] Test: mvn clean compile test.

## Followup
- mvn clean compile test
- ./mvnw spring-boot:run
- cd frontend && npm i && ng serve

