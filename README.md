# YOUTHX Backend

REST API for the YOUTHX Flutter application, built for the ACLEDA GenZ mobile app competition.

It exposes JWT-secured endpoints for authentication, growth (goals, todos, habits), finance (expenses, categories, saving goals), and community (posts, photos, comments, likes), backed by PostgreSQL.

## Overview

The service is a stateless Spring Boot REST API. Every authenticated endpoint is scoped to the user identified by the JWT in the `Authorization` header, so one user can never read or mutate another user's data — ownership is enforced in the service layer and violations return `403`.

Errors are normalised by a global exception handler into a single JSON shape (`ErrorResponse`: `timestamp`, `status`, `error`, `message`, `path`).

## Tech Stack

| Technology | Version | Notes |
|---|---|---|
| Java | **21** | `<java.version>21</java.version>` in `pom.xml` |
| Spring Boot | **3.5.16** | `spring-boot-starter-parent` |
| Spring Web | managed | REST controllers |
| Spring Data JPA / Hibernate | managed | `ddl-auto: validate` |
| Spring Security | managed | stateless filter chain |
| JJWT | **0.12.6** | `jjwt-api` / `jjwt-impl` / `jjwt-jackson` |
| PostgreSQL driver | managed | `scope: runtime` |
| Flyway | managed | `flyway-core` + `flyway-database-postgresql` |
| Springdoc OpenAPI | **2.9.1** | Swagger UI + `/v3/api-docs` |
| Lombok | managed | `optional` |
| Bean Validation | managed | `@Valid` on request bodies |
| Maven | — | `mvnw` / `mvnw.cmd` wrapper is committed |
| JUnit 5 | managed | via `spring-boot-starter-test` |

## Requirements

| Requirement | Value |
|---|---|
| JDK | **Java 21** |
| Maven | **3.6+**, or use the bundled wrapper (`./mvnw`, no local install needed) |
| PostgreSQL | any supported version (developed against a local PostgreSQL server) |

## Database Setup

Configuration from `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/youthx_db
    username: postgres
    password: ${DB_PASSWORD}
```

| Setting | Value |
|---|---|
| Database name | `youthx_db` |
| Host | `localhost` |
| Port | `5432` |
| Username | `postgres` |
| Password | supplied via the `DB_PASSWORD` environment variable |

**Schema mechanism: Flyway.** There is no `schema.sql` and no `ddl-auto: create/update` — Hibernate runs with `ddl-auto: validate`, so it verifies the schema matches the entities but never creates or alters it. All schema changes happen through versioned migrations that run automatically on startup:

| Migration | Purpose |
|---|---|
| `V1__create_schema.sql` | creates all tables |
| `V2__seed_data.sql` | seeds expense categories, badges, challenges |
| `V3__remove_gamification.sql` | drops gamification tables and `users.xp_points` |
| `V4__add_income_categories.sql` | adds income expense categories |

To prepare a database, create the empty database and start the application — Flyway applies every pending migration on boot:

```sql
CREATE DATABASE youthx_db;
```

No manual migration command is needed or supported.

Live tables: `users`, `expense_categories`, `posts`, `post_photos`, `comments`, `likes`, `goals`, `todos`, `habits`, `expenses`, `saving_goals`.

## Configuration

Two secrets are required and neither has a default value — the application fails fast without them.

| Variable | Used for |
|---|---|
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | HMAC signing key for JWTs |

Set them as environment variables:

```bash
# PowerShell
$env:DB_PASSWORD = "YOUR_DB_PASSWORD"
$env:JWT_SECRET  = "YOUR_JWT_SECRET_AT_LEAST_32_BYTES"
```

```bash
# bash / macOS / Linux
export DB_PASSWORD="YOUR_DB_PASSWORD"
export JWT_SECRET="YOUR_JWT_SECRET_AT_LEAST_32_BYTES"
```

Non-secret settings in `application.yaml`:

| Setting | Value | Meaning |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.flyway.enabled` | `true` | run migrations on startup |
| `spring.flyway.baseline-on-migrate` | `false` | never baseline a non-empty schema silently |
| `spring.jpa.hibernate.ddl-auto` | `validate` | verify schema, never mutate it |
| `spring.jpa.open-in-view` | `false` | no lazy-loading in the view layer |
| `spring.servlet.multipart.max-file-size` | `10MB` | post photo upload cap |
| `spring.servlet.multipart.max-request-size` | `50MB` | whole-request cap |
| `app.upload.dir` | `uploads` | directory for uploaded post photos |
| `jwt.expiration` | `604800000` (7 days) | token lifetime in ms |

`uploads/` is git-ignored; it is runtime data, not source.

**Never commit `DB_PASSWORD` or `JWT_SECRET`.** Use placeholders in documentation and supply real values through the environment.

## Run Backend

With the bundled Maven wrapper (no local Maven install required):

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Or build a jar and run it:

```bash
mvnw.cmd clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The API listens on `http://localhost:8080`. Interactive API docs are at `/swagger-ui.html`, and the OpenAPI JSON at `/v3/api-docs`.

## Test

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

## API Overview

All endpoints below are read directly from the current controllers. All require `Authorization: Bearer <token>` unless marked **public**.

### Auth — `/api/auth`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | create account, returns the user |
| POST | `/api/auth/login` | public | returns `{ token, user }` |
| POST | `/api/auth/logout` | required | stub — returns `200 OK`, does nothing server-side |

### User

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/users/me` | required | current user profile (read-only) |

### Growth

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/goals` | required | create goal |
| GET | `/goals` | required | list own goals |
| GET | `/goals/{id}` | required | get one goal |
| PUT | `/goals/{id}` | required | update goal |
| DELETE | `/goals/{id}` | required | delete goal |
| POST | `/todos` | required | create todo |
| GET | `/todos` | required | list own todos |
| PUT | `/todos/{id}` | required | update todo |
| DELETE | `/todos/{id}` | required | delete todo |
| POST | `/habits` | required | create habit |
| GET | `/habits` | required | list own habits |
| PUT | `/habits/{id}` | required | update habit |
| DELETE | `/habits/{id}` | required | delete habit |
| POST | `/habits/{id}/complete` | required | mark habit complete |
| POST | `/habits/{id}/uncomplete` | required | revert habit completion |

### Finance

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/expenses` | required | create expense |
| GET | `/expenses` | required | paged list of own expenses |
| PUT | `/expenses/{id}` | required | update expense |
| DELETE | `/expenses/{id}` | required | delete expense |
| GET | `/expense-categories` | required | list categories |
| POST | `/saving-goals` | required | create saving goal |
| GET | `/saving-goals` | required | list own saving goals |
| PUT | `/saving-goals/{id}` | required | update saving goal |
| DELETE | `/saving-goals/{id}` | required | delete saving goal |
| POST | `/saving-goals/{id}/deposit` | required | deposit into a saving goal |

### Community

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/posts` | required | create post |
| GET | `/posts` | required | paged feed |
| GET | `/posts/{id}` | required | single post |
| PUT | `/posts/{id}` | required | update post |
| DELETE | `/posts/{id}` | required | delete post |
| POST | `/posts/{postId}/photos` | required | upload a photo (multipart) |
| POST | `/posts/{postId}/comments` | required | add comment |
| GET | `/posts/{postId}/comments` | required | list comments |
| DELETE | `/posts/comments/{id}` | required | delete comment |
| POST | `/posts/{postId}/like` | required | toggle like |

### Static

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/uploads/**` | public | serve uploaded post photos from disk |

### Error mapping

| Status | Cause |
|---|---|
| `400` | `IllegalArgumentException`, validation failure |
| `401` | missing/expired/invalid JWT, `InvalidCredentialsException` |
| `403` | `ResourceOwnershipException` — touching another user's resource |
| `404` | `NoSuchElementException` |
| `409` | `DuplicateResourceException` |

## Security

High-level flow:

1. **Login.** `POST /api/auth/login` verifies the email and BCrypt-hashed password, then `JwtService` signs an HMAC JWT containing the user UUID using a key derived from `JWT_SECRET`, with a 7-day expiry (`jwt.expiration = 604800000` ms). The response is `{ token, user }`.
2. **Request.** The Flutter client stores that token and sends it as `Authorization: Bearer <token>`.
3. **Filter.** `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`. It requires the `Bearer ` prefix, validates signature and expiry via `JwtService`, extracts the user UUID, and sets the Spring `SecurityContext` authentication.
4. **Authorization.** `SecurityConfig` is stateless (`SessionCreationPolicy.STATELESS`) with CSRF disabled. Only `/api/auth/register`, `/api/auth/login`, the Swagger/OpenAPI paths, and `/uploads/**` are `permitAll()`; `anyRequest().authenticated()`. Unauthenticated requests get a JSON `401` from a custom `authenticationEntryPoint`, not an HTML login page.
5. **Ownership.** Controllers resolve the current user from the security context (never from client input) and services reject cross-user access with `ResourceOwnershipException` → `403`.
6. **Passwords** are hashed with `BCryptPasswordEncoder`; plaintext passwords are never stored or logged.

`JWT_SECRET` must stay out of version control and out of logs. It is read only from configuration.

## Flutter Connection

The Flutter app reaches this API over HTTP through Dio:

1. Build-time `--dart-define=API_BASE_URL=...` sets the base URL (`lib/core/network/app_config.dart`).
2. `ApiClient` configures Dio with that `baseUrl` and an interceptor that attaches the stored JWT.
3. `ApiProvider` wraps the Dio calls and converts failures into `ApiException(statusCode, message)`.
4. Repositories (`AuthRepository`, `RestGrowthRepository`, `RestFinanceRepository`, `RestCommunityRepository`) are bound in GetX bindings and own the request/response mapping.
5. The token is persisted with `flutter_secure_storage` and sent on every subsequent call.

**Local development.** A physical Android device or emulator cannot see `localhost` on the host. Either use the Android emulator's own loopback alias, or forward the port:

```bash
adb reverse tcp:8080 tcp:8080
```

then build/run the app with `--dart-define=API_BASE_URL=http://localhost:8080`. The Android manifest permits cleartext HTTP for local development.

**Release.** A reviewer's device cannot reach a developer machine's `localhost`. Release APKs must be built against a publicly reachable HTTPS backend URL.

If the backend is unreachable, the app surfaces a connection error rather than silently switching to fake data for REST-backed features.

## Database / Schema

The schema is **Flyway-controlled and versioned**. Treat it as a contract:

- Never edit an applied migration (`V1`–`V4`). Flyway checksums will fail on startup.
- Add a new `V5__...sql` for any change.
- Hibernate runs `ddl-auto: validate`, so entity changes without a matching migration will fail fast at boot rather than silently diverge.
- Data seeded by `V2`/`V4` (expense categories) is inserted with `INSERT`; re-running against an already-migrated database will not duplicate rows because Flyway records applied versions.

Current tables: `users`, `expense_categories`, `posts`, `post_photos`, `comments`, `likes`, `goals`, `todos`, `habits`, `expenses`, `saving_goals`.

## Known Limitations

Verified from the current source:

- **Server-side password reset is not implemented.** There is no reset endpoint, reset-token table, or mail delivery dependency. The Flutter app correspondingly shows plain text stating that reset is unavailable rather than a dead link.
- **Logout is a stub.** `POST /api/auth/logout` returns `200 OK` and performs no token invalidation; the token stays valid until it expires. Logout is effectively client-side.
- **Follower/following is not implemented.** No follow entity, table, or endpoint exists. The Flutter app renders those lists from local mock data.
- **Profile avatar upload is not implemented.** `GET /users/me` is read-only; there is no `PUT /users/me` and no avatar upload endpoint. Profile edits and avatars are stored locally on the device via `SharedPreferences`.
- **Notification infrastructure is not implemented.** There is no notification entity, table, or feed endpoint, so the app's notification screen is an explicit empty state.
- **Privacy settings are not enforced.** No `privacy_settings` table and no enforcement on reads; the Flutter toggles are UI-only.
- **Gamification was removed.** `V3` dropped `badges`, `challenges`, `user_badges`, `user_challenges`, and `users.xp_points`. There is no XP or badge API.
- **No story or messaging endpoints.** Stories ride on the community post repository; the Flutter messenger uses a mock repository with no backend.
- **No email verification.** `RegisterRequest`/`LoginRequest` carry no verification token and there is no verification endpoint.
- **No rate limiting** on auth endpoints, and **no refresh-token** mechanism — clients re-login after the 7-day expiry.
- **Uploaded photos are stored on local disk** under `app.upload.dir` and served from `/uploads/**`. There is no object storage, no image resizing, and no per-file authorisation on the public `/uploads/**` path.

## Development Rules

- **Never commit secrets.** `DB_PASSWORD` and `JWT_SECRET` stay in the environment. Use `YOUR_DB_PASSWORD`-style placeholders in any example or documentation.
- **Never change the schema without architecture approval.** Go through a new numbered Flyway migration (`V5__...`, `V6__...`). Do not edit `V1`–`V4`; checksum validation will break startup. Coordinate with the Flutter team, since entities and migrations must stay in sync.
- **Never silently change API contracts.** Request/response DTOs, status codes, and the `ErrorResponse` shape are consumed by the Flutter app. Breaking changes need a coordinated version bump on both sides, not a quiet field rename or removal.
- **Keep ownership enforcement server-side.** Always resolve the acting user from the security context, never from a request parameter.
- **Do not weaken the security config** to make a client test pass. `/api/auth/register`, `/api/auth/login`, Swagger paths, and `/uploads/**` are the only public routes.
- **Validate all request bodies** with `@Valid` and let `GlobalExceptionHandler` produce the response.
- **Run `./mvnw test` before pushing.**
- **Keep uploaded files out of git.** `uploads/` is ignored by design.