---
name: spring-boot-jpa
description: Change JPA entities, repositories, Flyway migrations, or transactional database workflows in Folio Pro.
---

# Spring Data JPA workflow

Use this skill when changing persisted data, repository queries, or transactional service workflows.

## Project conventions

- PostgreSQL is the application datasource configured in `src/main/resources/application.yml`.
  H2 is configured for tests only.
- JPA entities live under `com.foliopro.api.model`; Spring Data repositories live under
  `com.foliopro.api.repository`; transaction boundaries belong in services.
- Flyway owns production schema evolution. Do not rely on Hibernate schema creation.
- Preserve market-specific table relationships: USA items reference USA watchlists and India items
  reference India watchlists.
- Keep HTTP concerns out of entities/repositories and preserve typed persistence models.
- Use `@Transactional(readOnly = true)` for read workflows and `@Transactional` for mutations or
  multi-step operations that require atomicity.
- Let the shared `LoggingAspect` record service method lifecycle and duration; do not add repetitive
  service-level entry/exit logs or log entity/request contents. Propagate exceptions for centralized
  API handling.
- Avoid N+1 queries when mapping entities to response DTOs.

## Implementation sequence

1. Inspect affected entities, repositories, service workflows, datasource config, migrations, and
   tests.
2. Make schema changes explicit, including nullability, unique constraints, indexes, foreign keys,
   and cascade behavior.
3. Keep business decisions in services and repository methods focused on data access.
4. Add focused tests using the test profile; do not require local PostgreSQL.
5. Update DTOs, OpenAPI, MockMvc tests, Postman, README, and related docs when data is exposed over
   HTTP.
6. Run targeted tests and a Maven package/compile.
7. Review affected skills and operational documentation.

## Verification checklist

- PostgreSQL remains the application datasource and H2 remains test-only.
- Transaction boundaries cover intended writes.
- Queries match the current schema and do not leak lazy entities to the API.
- Duplicate and missing-resource behavior is explicit.
- No shared item table or incorrect cross-market foreign key is reintroduced.
