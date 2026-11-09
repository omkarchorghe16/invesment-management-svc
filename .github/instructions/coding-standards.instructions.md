---
applyTo: "**/*.java"
---

# Java coding standards

- Use Java 21 and the existing Spring Boot, Maven, and project conventions.
- Prefer cohesive classes and methods with explicit types and clear names.
- Keep controllers limited to HTTP mapping, boundary validation, service delegation, and response
  handling.
- Use constructor injection and immutable dependencies.
- Validate external input at the boundary with Jakarta Bean Validation and explicit constraints;
  keep use-case rules in the service layer.
- Preserve type safety. Avoid unchecked casts, `Object`-typed APIs, and unnecessary abstractions.
- Handle expected failures explicitly. Do not swallow exceptions or return success-shaped fallbacks.
- Keep credentials outside source-controlled files; use environment variables or ignored local files.
- Add focused tests for changed behavior and keep OpenAPI, Postman, and README contract details in
  sync when public APIs change.
- Keep package names lowercase and aligned with source directories.
- Use the shared AOP invocation logger for controller/service lifecycle telemetry; do not add
  repetitive method-level logs or log request/DTO contents.
- Run the smallest relevant existing Maven test/build commands after changes.
