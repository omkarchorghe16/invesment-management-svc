---
name: spring-boot-provider-integration
description: Add an external market-data provider integration with typed parsing, bounded HTTP behavior, safe errors, and tests.
---

# External provider integration workflow

Use this skill only when an external market-data provider is explicitly introduced. The current
`SampleMarketDataProvider` is fixture-backed and does not make live provider requests.

## Project conventions

- Keep provider HTTP and response parsing in provider-specific clients; keep orchestration and
  persistence in services.
- Reuse existing HTTP infrastructure where available and apply explicit connection/read timeouts.
- Bind provider URLs, credentials, limits, and switches through Spring configuration properties.
  Keep real credentials in environment variables or ignored local secrets files.
- Normalize symbols at the service boundary and map provider results to typed models.
- Make rate limits, retries, caching, and failure behavior explicit. Do not return empty
  success-shaped results for upstream failures.
- Never log credentials, authorization headers, full secret-bearing URLs, or raw provider payloads.

## Implementation sequence

1. Inspect configuration, provider client, service, persistence, tests, and documented API/limits.
2. Add safe configuration defaults without committing credentials.
3. Implement bounded request/response handling and explicit provider error mapping.
4. Keep orchestration and persistence in the service layer.
5. Add deterministic tests for parsing, malformed responses, HTTP errors, missing config, and limits
   as relevant; never call live providers in tests.
6. Update OpenAPI, Postman, README, and affected skills/docs for public behavior changes.
7. Run targeted tests and Maven package/compile.

## Verification checklist

- Network calls have bounded timeouts and explicit failure behavior.
- Rate limit and cache behavior match the approved provider contract.
- Errors do not expose credentials or upstream payloads.
- Tests do not require network access or live provider credentials.
