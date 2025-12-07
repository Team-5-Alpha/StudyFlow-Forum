# StudyFlow Forum

Monorepo with a Spring Boot backend and React/Vite frontend for a forum platform featuring roles, posts, comments, notifications, and JWT-based authentication.

## Swagger / OpenAPI
- Swagger UI (after starting the backend): http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Database Schema
- ER diagram:
  - ![Database schema diagram](study-flow-er-diagram.png)

## Hosted Project
- No public deployment at the moment. When run locally, the frontend is served at http://localhost:5173.

## Local Setup & Run
### Prerequisites
- Java 17
- Node.js 18+ and npm
- MariaDB/MySQL instance

### Backend
1) Update DB credentials/URL in `backend/src/main/resources/application.properties`.
2) From the project root:
   ```bash
   ./gradlew :backend:bootRun
   ```
   Backend listens on `http://localhost:8080`.

### Frontend
1) Install dependencies:
   ```bash
   cd ..
   cd frontend
   npm install
   ```
2) Start the dev server:
   ```bash
   npm run dev
   ```
   Frontend is available at `http://localhost:5173`.

## Tests
- Backend unit/integration tests:
  ```bash
  ./gradlew :backend:test
  ```
- Frontend lint (optional):
  ```bash
  cd frontend && npm run lint
  ```

## Tech Stack
- Backend: Spring Boot 3, Spring Security (JWT), JPA/Hibernate, MariaDB
- Frontend: React 19, Vite, React Router, Axios
