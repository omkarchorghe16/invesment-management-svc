---
name: spring-boot-testing
description: Add and run focused JUnit tests for Folio Pro's Spring Boot controllers, services, and persistence.
---

# Spring Boot testing workflow

Use this skill when adding behavior, fixing regressions, changing endpoints, or validating database
workflows.

## Project conventions

- Use the existing Maven test stack: JUnit 5, Spring Boot Test, Mockito, MockMvc, and H2. Do not add
  a test dependency for routine coverage without a clear need.
- Use existing integration-test conventions and test configuration. Tests must not require a
  developer's PostgreSQL instance or external network access.
- Keep fixtures synthetic; never use real credentials, personal information, or provider payloads.
- Public API changes require coverage for response contract, statuses, validation, and relevant
  service behavior.
- Controller/service invocation logging is centralized in `LoggingAspect`; test that aspect keeps
  successful return values and thrown exceptions unchanged when changing its behavior.
- Database tests should cover constraints, market separation, relationships, and transaction
  behavior where relevant.

## Implementation sequence

1. Locate nearby tests and follow their naming/setup conventions.
2. Test at the responsible boundary: controller contract, service rule, or persistence behavior.
3. Cover normal behavior and the most important invalid/error path.
4. Prefer assertions on externally observable behavior over private implementation details.
5. Run the affected tests first, then broaden to `mvn test` if needed.
6. If a test fails, distinguish product regressions from environment/test setup issues; do not weaken
   assertions simply to pass.
7. Update related skills, README, and directly related docs when behavior or test workflow changes.

## Verification checklist

- Tests are deterministic and isolated from external systems.
- New public API behavior has web/controller coverage.
- Market-specific persistence changes preserve the correct foreign key and table.
- The targeted Maven command exits successfully before reporting verification.
