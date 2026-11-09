---
name: spring-boot-architecture
description: Change application architecture, boundaries, cross-cutting behavior, or persistence topology in Folio Pro.
---

# Spring Boot architecture workflow

Use this skill when changes span layers, move responsibilities, alter shared conventions, or
introduce a subsystem.

## Current application boundaries

- `controller` owns HTTP mappings, validation, and response construction.
- `service` coordinates use cases and transaction boundaries.
- `model` contains JPA persistence entities; `repository` contains Spring Data access.
- PostgreSQL is the application datasource; H2 is for tests.
- USA and India watchlists/items have separate persisted tables and market-specific relationships.
- Docker Compose is the local container workflow. Optional `k8s/` and
  `scripts/deploy-local-k8s.sh` target Docker Desktop Kubernetes only.

## Architecture rules

- Preserve one-way dependencies: controllers -> services -> repositories.
- Keep public DTOs distinct from JPA entities and keep persistence logic out of controllers.
- Use service-level transaction boundaries for database workflows.
- Treat schema changes as explicit Flyway migrations and preserve intended foreign keys,
  uniqueness, and lifecycle behavior.
- Keep credentials outside tracked files and redact them from logs and error responses.
- Keep controller/service lifecycle telemetry in `LoggingAspect` and final error reporting in
  `ApiExceptionHandler`; avoid duplicate logs and never log method arguments or request bodies.
- Do not introduce a new datasource, migration framework, security mechanism, or architecture
  pattern without confirming compatibility and intended behavior.
- Keep OpenAPI, Postman, README, and deployment documentation synchronized with behavior.

## Implementation sequence

1. Trace the relevant controller/service/repository/entity/configuration and tests.
2. Select the narrowest layer that owns the requested behavior.
3. Identify impacts on schema, transactions, API contracts, runtime config, and deployment.
4. Implement a focused change and add tests at affected boundaries.
5. Review the skills under `.github/skills/` and update those affected by the change.
6. Update README and directly related API, configuration, or operational docs.
7. Run the smallest relevant tests/build and report remaining environment limitations.

## Review checklist

- Responsibilities follow the existing layers.
- Schema and transaction semantics are explicit.
- Market-specific data remains in its matching tables.
- Tests do not depend on live services.
- Relevant docs and skills match implemented behavior; no secrets are exposed.
