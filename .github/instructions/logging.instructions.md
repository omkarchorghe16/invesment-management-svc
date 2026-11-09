---
applyTo: "**/*.java"
---

# Logging standards

- Use SLF4J through an existing class logger; do not use `System.out` or `System.err`.
- Log useful lifecycle events at INFO, recoverable problems at WARN, and unexpected failures at
  ERROR. Use DEBUG for diagnostic detail that is too noisy for normal operation.
- Include safe context such as operation, market, record count, or status.
- Never log passwords, tokens, authorization headers, secret-bearing URLs, or full sensitive
  request/response payloads.
- Do not log the same exception at multiple layers. Propagate failures when the request must fail.
- Use parameterized log messages and avoid logging entire request objects or lists.
- `LoggingAspect` logs start/completion and duration for public controller/service methods. It
  deliberately omits method arguments and request bodies. Keep exception propagation intact;
  `ApiExceptionHandler` records handled client failures and logs unexpected exceptions with their
  stack trace.
- Keep request context limited to HTTP method and sanitized path; never add query strings,
  credentials, or payloads to access logs.
