---
name: spring-boot-rest-api
description: Implement or change REST endpoints, validation, OpenAPI documentation, and Postman examples in Folio Pro.
---

# Spring Boot REST API workflow

Use this skill when changing HTTP routes, payloads, response status codes, or controller tests.

## Project conventions

- Put controllers under `com.foliopro.api.controller` and transport types in
  `com.foliopro.api.dto`.
- Keep controllers limited to HTTP mapping, boundary validation, service delegation, and response
  handling. Do not call repositories directly.
- Use typed request/response DTOs and Jakarta Bean Validation.
- Preserve established exception handling and use explicit status codes.
- `LoggingAspect` records controller/service invocation lifecycle and duration without logging
  arguments or payloads. Keep errors propagating to `ApiExceptionHandler` for consistent responses
  and server-side exception reporting.
- Document endpoints with springdoc OpenAPI annotations.
- Keep public APIs and examples in `README.md` and `postman/Folio-Pro-API.postman_collection.json`
  synchronized.

## Implementation sequence

1. Inspect the controller, service, DTOs, exception handler, OpenAPI config, Postman examples, and
   nearby tests.
2. Keep business rules in the service and validate HTTP input at the boundary.
3. Add/update mapping and OpenAPI documentation.
4. Add MockMvc coverage for success, response shape/status, validation, and delegation.
5. Update Postman and README for public contract changes.
6. Run targeted tests and package/compile if appropriate.
7. Review affected skills and directly related docs.

## Verification checklist

- Route and method are correct and do not conflict with existing mappings.
- Success and failure status codes match the API behavior.
- OpenAPI, Postman, and README agree with actual DTO fields and constraints.
- Tests are isolated from live services and databases.
- No credentials or sensitive values are added to logs or fixtures.
