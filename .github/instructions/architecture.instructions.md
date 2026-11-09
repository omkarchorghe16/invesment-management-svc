---
applyTo: "**/*.java"
---

# Architecture standards

- Keep the application layered: `controller` handles HTTP; `service` coordinates use cases and
  transactions; `model` and `repository` own persistence.
- Controllers must not call repositories directly. Keep JPA entities focused on persistence, not
  HTTP behavior or orchestration.
- Keep dependencies one-way: controllers -> services -> repositories; avoid cyclic dependencies.
- Use service-level transaction boundaries for multi-step persistence workflows.
- PostgreSQL is the production/application datasource; H2 is used for tests only.
- USA and India watchlists and items are stored in their corresponding tables. Keep those database
  relationships and Flyway migrations explicit.
- Keep public transport DTOs separate from persistence entities.
- Keep configuration in Spring configuration files/properties and environment variables; never
  hard-code credentials.
- Keep controller/service lifecycle telemetry in `LoggingAspect`; exception handlers own final API
  error logging. Do not add duplicate per-method logs or swallow exceptions.
- Keep Docker Compose as the local container workflow and Kubernetes manifests as an optional
  Docker Desktop-only workflow. Do not commit Kubernetes Secrets or local credentials.
- For behavior changes, review the skills under `.github/skills/`, update affected skills and
  directly related docs, and keep README, OpenAPI, Postman, and deployment config consistent.
