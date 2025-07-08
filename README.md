# DaRoomate

## Overview

DaRoomate is a full-stack web application for roomates featuring a Spring Boot backend and a React frontend. It provides user authentication, profile management, friend/roomate connections, and real-time messaging using WebSockets. The backend uses PostgreSQL for data storage and supports OAuth2/JWT authentication (with Auth0 integration). Docker Compose is used for local development and testing environments.

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
- **Framework:** Spring Boot 3.4.4
- **Database:** PostgreSQL
- **Authentication:** OAuth2/JWT (Auth0)
- **WebSocket:** STOMP over SockJS
- **Build Tool:** Maven

### Key Endpoints (examples)

- `PUT /api/additional_info` — Add user profile info
- `GET /api/profile-status` — Check if user profile is complete
- `GET /api/create_or_find_user` — Create or fetch user by Auth0 ID
- `PUT /api/profile/updateEmail` — Update user email
- `POST /api/friend/addFriend` — Send friend request
- `POST /api/friend/accept/{requestId}` — Accept friend request
- `GET /api/friend/getfriends` — List friends
- `GET /api/messages/{senderId}/{recipientId}` — Get chat messages
- `PUT /api/messages/read/{messageId}` — Mark message as read
- `GET /api/messages/chats/{userId}` — List user chats
- `@MessageMapping /chat` — WebSocket endpoint for real-time chat

### Environment Variables

Set these in your environment or a `.env` file (for Docker Compose):

- `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB` — PostgreSQL credentials
- `POSTGRESQL_HOST`, `POSTGRESQL_PORT`, `POSTGRESQL_DATABASE`, `POSTGRESQL_USERNAME`, `POSTGRESQL_PASSWORD` — For Spring datasource
- `AUTH_DOMAIN`, `AUTH_CLIENT`, `AUTH_SECRET` — Auth0 credentials
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
- **WebSocket:** STOMP/SockJS
- **Auth:** Auth0 React SDK

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

## License

This project is currently unlicensed. Add your license information here if needed.
