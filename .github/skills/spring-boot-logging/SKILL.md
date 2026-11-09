---
name: spring-boot-logging
description: Add or change application observability and safe logging in Folio Pro.
---

# Logging and observability workflow

Use this skill when changing logs, exception reporting, or request-level diagnostics.

## Project conventions

- `LoggingAspect` records start/completion and elapsed time for public methods in controller and
  service packages. It logs the HTTP method and sanitized path when a servlet request is active.
- The aspect never logs arguments, DTOs, file contents, or query strings. Keep logs safe for
  credentials and user data.
- Aspect advice must propagate the original exception unchanged; do not convert failures into
  success-shaped values.
- `ApiExceptionHandler` logs handled 4xx failures without payloads and logs unexpected exceptions
  with stack traces. Preserve its sanitized client response.
- Application logs use SLF4J and are emitted to standard output. `APP_LOG_LEVEL` controls the
  `com.foliopro.api` package log level; Docker logs are accessible with
  `docker compose logs -f folio-pro-api`.
- Log a throwable stack trace only at the error-handling boundary; avoid repeated stack traces at
  each service/controller boundary.

## Implementation sequence

1. Identify the operation and layer that owns the diagnostic event.
2. Prefer the shared aspect for controller/service lifecycle telemetry over repetitive inline logs.
3. Keep exception propagation intact and log actionable context without request content or secrets.
4. Add tests for unchanged method results and exception propagation when modifying the aspect.
5. Update README and relevant instructions/skills when logging behavior or configuration changes.
6. Run targeted tests and verify emitted logs contain no sensitive inputs.

## Verification checklist

- Controller and service calls log method identity, outcome, and duration.
- Exceptions remain visible to the established API exception handler.
- Logs omit arguments, payloads, query strings, and credentials.
- 4xx and 5xx handling remains consistent with the API response contract.
