# DaRoomate

## Overview

DaRoomate is a full-stack web application for roomates featuring a Spring Boot backend and a React frontend. It provides user authentication, profile management, friend/roomate connections, and real-time messaging using WebSockets. The backend uses PostgreSQL for data storage and supports JWT authentication.

---

## Directory Structure

```
ProjectStart/
  backend/         # Spring Boot backend (Java 21, Maven)
    src/           # Main backend source code
    docker/        # Docker Compose for backend services
    dockerTest/    # Docker Compose for test environment
    practiceCode/  # Additional backend code and utilities
  frontend/        # React frontend (Node.js, npm)
    src/           # Main frontend source code
```

---

## Backend (Spring Boot)

- **Language:** Java 21
- **Framework:** Spring Boot
- **Database:** PostgreSQL
- **Authentication:** OAuth2/JWT (Spring Security + JJWT)
- **Build Tool:** Maven

### Environment Variables

Set these in your environment or a `.env` file (for Docker Compose):

- `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB` — PostgreSQL credentials
- `POSTGRESQL_HOST`, `POSTGRESQL_PORT`, `POSTGRESQL_DATABASE`, `POSTGRESQL_USERNAME`, `POSTGRESQL_PASSWORD` — For Spring datasource
- `CONTAINER_PORT` — (optional) Backend port (default: 8080)

### Database

- Schema and seed data are in `backend/src/main/resources/data/`
  - `schema.sql` — Table definitions for users, roles, credentials, documents, etc.
  - `data.sql` — Example seed data

---

## Frontend (React)

- **Language:** JavaScript (React 19)
- **State Management:** React Context/State
- **Routing:** React Router

### Scripts

Run these in the `frontend/` directory:

- `npm install` — Install dependencies
- `npm start` — Start development server (http://localhost:3000)
- `npm run build` — Build for production
- `npm test` — Run tests

---

## Running Locally

### Prerequisites
- Java 21
- Node.js (v18+ recommended)
- Docker & Docker Compose

### Backend

1. Copy or set environment variables as above.
2. Start PostgreSQL (see Docker section) or use your own instance.
3. In `backend/`, build and run:
   ```sh
   ./mvnw spring-boot:run
   ```

### Frontend

1. In `frontend/`, install dependencies:
   ```sh
   npm install
   ```
2. Start the dev server:
   ```sh
   npm start
   ```

### Docker Compose (for DB)

- In `backend/docker/`:
  ```sh
  docker compose up -d
  ```
- For test DB, use `backend/dockerTest/compose.yml`.

---

## Testing

- **Backend:**
  - Run with Maven: `./mvnw test`
  - Tests in `backend/src/test/java/`
- **Frontend:**
  - Run with npm: `npm test`

---

## Contribution

1. Fork the repo and create a feature branch.
2. Make your changes and add tests.
3. Submit a pull request.

---
