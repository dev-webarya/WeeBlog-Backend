# Blog Application — Walkthrough

## Architecture

Spring Boot **3.4.2** modular monolith with **MongoDB**, Java 17, Swagger (OpenAPI).

```
com.blogapp/
├── config/           → SwaggerConfig, WebConfig, MongoAuditConfig
├── common/           → GlobalExceptionHandler, DTOs, Utilities
├── blog/             → BlogPost entity, search, archive, sorting
├── reaction/         → Like/Dislike toggle with rate limiting
├── comment/          → Comments with honeypot anti-spam
├── subscriber/       → Email subscription with OTP verification
├── otp/              → OTP generation, hashing, email delivery
├── submission/       → 3-step blog submission (start→verify→finish)
└── admin/            → Blog moderation + comment moderation
```

## Modules Summary

| Module | Key Features |
|--------|-------------|
| **Blog** | Text-indexed search (title+excerpt), archive aggregation (year→month), sort by recent/popular/oldest |
| **Reaction** | Like/Dislike toggle, switch behavior, per-visitor rate limiting, IP hashing |
| **Comment** | Honeypot anti-spam field, IP-based rate limiting, admin hide/delete |
| **OTP** | 6-digit, SHA-256 hashed, 10-min expiry, 5 max attempts, 60s resend cooldown |
| **Email** | HTML templates for OTP, approval, rejection, submission confirmation |
| **Submission** | 3-step flow: start → verify OTP → finish (creates PENDING blog) |
| **Subscriber** | Subscribe with OTP verification, unsubscribe, admin listing |
| **Admin** | Approve/reject blogs (email notifications), edit, comment moderation, subscriber management |

## API Endpoints

| Group | Endpoints |
|-------|-----------|
| **Public Blog** | `GET /api/blogs`, `GET /api/blogs/archive`, `GET /api/blogs/{slug}` |
| **Reaction** | `POST /api/blogs/{id}/reaction`, `GET /api/blogs/{id}/reaction` |
| **Comment** | `GET /api/blogs/{id}/comments`, `POST /api/blogs/{id}/comments` |
| **Subscribe** | `POST /api/blogs/subscribe/start`, `POST .../verify-otp`, `POST .../unsubscribe` |
| **Submission** | `POST /api/blogs/submission/start`, `POST .../verify`, `POST .../finish` |
| **Admin Blog** | `GET /api/admin/blogs`, `POST .../approve`, `POST .../reject`, `PATCH .../edit` |
| **Admin Comments** | `GET /api/admin/comments/pending`, `POST .../{id}/hide`, `DELETE .../{id}` |
| **Admin Subscribers** | `GET /api/admin/subscribers` |

## Build & Run

```bash
# Compile
.\mvnw.cmd clean compile

# Run (requires MongoDB on localhost:27017)
.\mvnw.cmd spring-boot:run

# Swagger UI
http://localhost:8080/swagger-ui.html
```

## Tech Stack

- **Spring Boot 3.4.2** + Java 17
- **MongoDB** (spring-data-mongodb)
- **Swagger** (springdoc-openapi 2.8.3)
- **Jsoup** (HTML sanitization / XSS prevention)
- **Spring Mail** (Gmail SMTP for OTP emails)
- **Lombok** (boilerplate reduction)
