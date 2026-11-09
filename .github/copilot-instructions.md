# Repository instructions

For code changes, follow the applicable guidance in:

- `.github/instructions/coding-standards.instructions.md`
- `.github/instructions/architecture.instructions.md`
- `.github/instructions/spring-boot.instructions.md`
- `.github/instructions/logging.instructions.md`

Reusable workflows are in `.github/skills/*/SKILL.md`. Use the relevant skill for architecture,
JPA, REST API, testing, or future external market-data integration work. These skills supplement
the repository-wide instructions.

Keep changes focused, preserve existing behavior unless a behavior change is requested, and add or
update tests for changed behavior. PostgreSQL is the authoritative application database and H2 is
for tests. Keep USA and India watchlist persistence separate. Never commit credentials, log
secrets, or expose private runtime configuration.

For user-visible or operational changes, update `README.md` and directly related documentation,
including OpenAPI, Postman, configuration, Docker, and Kubernetes examples where applicable.
