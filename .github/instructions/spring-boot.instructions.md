---
applyTo: "**/*.java"
---

# Spring Boot standards

- Use the existing Java 21, Spring Boot, and Maven dependency-management setup.
- Use the narrowest appropriate Spring stereotype and prefer constructor injection.
- Bind runtime configuration through Spring properties; do not read environment variables from
  business logic.
- Map HTTP payloads to typed DTOs and validate with Jakarta Validation.
- Return intentional HTTP statuses and use the established API exception handling.
- Keep persistence mappings and repository methods consistent with the Flyway-managed PostgreSQL
  schema; H2 is configured for tests.
- Document REST endpoints using springdoc OpenAPI and keep `/v3/api-docs`, Swagger UI, and Postman
  examples accurate.
- Test web mappings and database behavior with the existing test profile; tests must not depend on
  a developer's PostgreSQL server or live external API.
- Keep controller/service execution logging centralized in `LoggingAspect`; do not log arguments or
  request bodies. Preserve exception propagation so `ApiExceptionHandler` can return the intended
  response and record unexpected stack traces.
- For public contract or behavior changes, update affected skills, README, and directly related
  API/configuration/operational docs.
