# AGENTS

Canonical AI coding guidance for this repository. Keep this file as the source of truth; `CLAUDE.md` points here.

## Scope
- Monorepo with Spring Boot backend (`backend`), CRA frontend (`frontend`), and deployment assets (`deploy`).
- API boundary: `/user/**` (auth/public-ish) vs `/api/**` (authenticated) in `backend/src/main/java/com/roomate/app/config/security/SecurityConfig.java`.

## Big-Picture Architecture
- Backend layering is stable: `controller -> service -> repository -> entities`; app entry is `backend/src/main/java/com/roomate/app/StartOneApplication.java`.
- Core room domain is orchestrated in `backend/src/main/java/com/roomate/app/service/implementation/RoomServiceImplt.java` with `RoomEntity` and `RoomMemberEntity`.
- Room rules are enforced server-side (not UI): max 3 rooms/user, max 6 members/room, role-gated membership actions.
- Frontend state is centralized in `frontend/src/App.jsx`; pages are expected to mutate cached app data helpers rather than refetching broadly.
- Use `frontend/src/apiClient.js` for HTTP (`withCredentials: true`, base URL from `REACT_APP_BASE_API_URL`).

## Auth and Request Flow
- `/user/login` (`AuthController`) sets a single HttpOnly `jwt` cookie (no JWT in body). Cookie `maxAge` matches JWT expiry (4h).
- `JwtAuthenticationFilter` validates JWT from `jwt` cookie only (stateless, no bearer fallback).
- **CSRF Protection**: Enabled via double-submit cookie pattern (`CookieCsrfTokenRepository` in `SecurityConfig`).
  - Token also returned as `csrfToken` in `/user/status` JSON; frontend sends `X-CSRF-TOKEN` on mutations (`apiClient.js`).
  - CSRF-ignored endpoints: `/user/login`, `/user/register`, `/user/logout`, `/user/verify`.
- **Railway production**: frontend nginx proxies `/api/` and `/user/` to the backend private URL (`BACKEND_UPSTREAM`). Browser stays same-origin → `SameSite=Lax` cookies work; avoid pointing `REACT_APP_BASE_API_URL` at the public API host.
- Frontend boot path is `/user/status` -> `/api/get-user` -> `/api/profile-status` (see `frontend/src/App.jsx`).
  - `/user/status` always returns 200 with `authenticated` + `csrfToken` (even when logged out).
- `apiClient.js` automatically adds CSRF token to POST/PUT/DELETE/PATCH requests via interceptor.
- Room-scoped mutations use `RoomAuthorizationService` (`assertRoomMember` / `assertRoomRole`) — head/assistant for invite and chore/utility mutates.
- `frontend/src/component/userProfileRedirection.jsx` still uses Auth0 hooks; treat this as mixed/legacy auth context.

## Developer Workflows
- Backend local: `cd backend && mvn spring-boot:run` (requires Postgres + Redis).
- Backend docker stack: `cd backend && docker compose up` (API 8085, Postgres mapped to 5433).
- Backend tests: `cd backend && mvn test` (H2 + `test` profile from `backend/src/test/resources/application-test.yml`).
- Frontend local: `cd frontend && npm start` with `REACT_APP_BASE_API_URL=http://localhost:8085`; build: `npm run build`; tests: `npm test`.
- Production: Railway — frontend Dockerfile + `nginx.conf` (SPA + API proxy), backend Dockerfile. Set frontend `BACKEND_UPSTREAM` and leave `REACT_APP_BASE_API_URL` empty.

## Project-Specific Conventions
- Service implementation names are intentionally inconsistent (`*Implt`, `*Impl`, `UserServiceImplementation`); match existing naming in touched area.
- Controllers commonly use broad `try/catch` and return `ResponseEntity`; domain errors are often mapped via `UserApiError`.
- Prefer DTO-first contracts in `backend/src/main/java/com/roomate/app/dto`; avoid exposing entities unless an endpoint already does.
- Frontend role checks should use `frontend/src/constants/roles.jsx` constants, not raw role strings.
- Preserve eager vs lazy app-data loading behavior in `frontend/src/App.jsx` when adding UI data fetches.

## Integrations and Operational Notes
- **Security**: CSRF double-submit cookie pattern (`SecurityConfig` + `CsrfCookieFilter`); actuator limited to `health`/`info` anonymously; rate limiting is Bucket4j + Redis (fail-open when Redis down; disabled under `test` profile).
- Rate limiting is `RateLimitingFilter`, `RedisRateLimitConfig`; intentionally fail-open when Redis is down.
- Email invite/verification relies on SMTP env vars (`EMAIL_*`) via `RoomInviteMailSender` and `UserServiceImplementation`.
- WebSocket chat is scaffolded but inactive (`backend/src/main/java/com/roomate/app/websocket/WebSocketConfig.java` is commented; `frontend/src/webpages/Message.jsx` is placeholder).
- `DataSeeder` may auto-insert local test user/room data; account for this when debugging duplicate-looking data.
- JPA is `ddl-auto: update` and Flyway is disabled, so schema drift between environments is possible.

## Environment Variables (high-impact)
- Backend required: `POSTGRESQL_*`, `JWT_KEY` (>=32 chars), `EMAIL_HOST`, `EMAIL_PORT`, `EMAIL_ID`, `EMAIL_PASSWORD`.
- Backend optional: `ACTIVE_PROFILE` (`prod`/`dev`), `CONTAINER_PORT` (default `8085`).
- Frontend build-time: `REACT_APP_BASE_API_URL`.

