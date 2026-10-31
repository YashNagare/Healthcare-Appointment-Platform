# Auth Service

The Auth Service is the authentication component of the Healthcare Appointment Platform. It provides account registration and login, issues signed JWT access tokens and opaque refresh tokens, and exposes the authenticated user's profile.

## Features

- Patient registration and email/password login
- BCrypt password hashing (work factor 12)
- Signed JWT access tokens with user ID, email, and role claims
- Cryptographically random refresh tokens; only their SHA-256 hashes are stored
- Refresh-token rotation and revocation on refresh and logout
- Stateless Spring Security authentication for protected endpoints
- PostgreSQL persistence through Spring Data JPA

New registrations receive the `PATIENT` role. The current service requires authentication for protected routes but does not define role-specific authorization rules.

## Technology

- Java 21
- Spring Boot 4.1.2
- Spring Web, Spring Security, Spring Data JPA, and Bean Validation
- PostgreSQL
- JJWT 0.12.6
- Maven Wrapper

## Requirements

- JDK 21
- PostgreSQL
- A Base64-encoded JWT signing secret containing at least 256 bits of key material

## Configuration

The service listens on port `8081` by default. It requires a PostgreSQL database and JWT settings. Spring Boot environment variables can override the corresponding properties:

| Environment variable | Purpose |
| --- | --- |
| `SERVER_PORT` | HTTP port (default: `8081`) |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL, for example `jdbc:postgresql://localhost:5432/healthcare_appointment_platform` |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `APP_JWT_SECRET` | Base64-encoded signing secret; keep it private and use a different value in each environment |
| `APP_JWT_ACCESS_EXPIRATION_MS` | Access-token lifetime in milliseconds (default configuration: 900000, or 15 minutes) |
| `APP_JWT_REFRESH_EXPIRATION_MS` | Refresh-token lifetime in milliseconds (default configuration: 604800000, or 7 days) |

Create the PostgreSQL database before starting the service. For example, with PostgreSQL's command-line tools:

```shell
createdb healthcare_appointment_platform
```

Generate a signing secret locally instead of using a checked-in or shared value:

```shell
openssl rand -base64 32
```

Set the generated value as `APP_JWT_SECRET` in your local environment or secret manager. Do not commit signing secrets or database credentials. The current local `application.properties` contains development configuration; override its values with environment variables and use secret management for deployed environments.

Hibernate is currently configured with `spring.jpa.hibernate.ddl-auto=update` to update the schema during startup. Review this setting before deployment; production environments should use a controlled schema migration strategy.

## Run locally

From the repository root, open a terminal in this directory:

```shell
cd Auth-Service
```

Set the required environment variables for your shell, then start the application with the Maven Wrapper.

Windows PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/healthcare_appointment_platform"
$env:SPRING_DATASOURCE_USERNAME = "your_database_user"
$env:SPRING_DATASOURCE_PASSWORD = "your_database_password"
$env:APP_JWT_SECRET = "your_base64_encoded_secret"
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```shell
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/healthcare_appointment_platform"
export SPRING_DATASOURCE_USERNAME="your_database_user"
export SPRING_DATASOURCE_PASSWORD="your_database_password"
export APP_JWT_SECRET="your_base64_encoded_secret"
./mvnw spring-boot:run
```

The API base URL is `http://localhost:8081`. The context path is `/api/v1/auth`.

## API

All request and response bodies use JSON. Email addresses are trimmed and normalized to lowercase.

| Method | Endpoint | Description | Status |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Register a patient account | Implemented |
| `POST` | `/api/v1/auth/login` | Authenticate and issue access/refresh tokens | Implemented |
| `POST` | `/api/v1/auth/refresh` | Rotate refresh token and issue a new token pair | Implemented |
| `POST` | `/api/v1/auth/logout` | Revoke a refresh token | Implemented |
| `GET` | `/api/v1/auth/me` | Get the authenticated user's profile | Implemented |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset | **Pending development** |
| `POST` | `/api/v1/auth/reset-password` | Reset password | **Pending development** |

Protected endpoints require `Authorization: Bearer <access-token>`.

## Build and test

Run the test suite:

```shell
./mvnw test
```

On Windows, use `.\mvnw.cmd test`. The Spring application context test requires the service's configured dependencies, including a reachable PostgreSQL database and JWT secret.

Build the executable JAR:

```shell
./mvnw clean package
```

Run the packaged application:

```shell
java -jar target/Auth-Service-0.0.1-SNAPSHOT.jar
```

## Project layout

```text
src/main/java/com/healthcare/appointment/platform/Auth_Service/
├── config/       # Spring Security and JWT request filter
├── controller/   # Authentication REST endpoints
├── dto/          # API request and response models
├── entity/       # User and refresh-token persistence models
├── repository/   # Spring Data JPA repositories
└── service/      # Authentication flows and JWT operations
```
