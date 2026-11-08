# Folio Pro API

Java 21 / Spring Boot REST backend for the Angular Folio Pro prototype. The frontend repository is `omkarchorghe16/personal-wealth-management-angular`; this API keeps its US/India watchlist workflow and DTO fields compatible with `wealth.models.ts`. The implementation follows the Java 21 / Spring Web / JPA / PostgreSQL / OpenAPI approach used by the referenced `news-scheduler-svc`.

## Frontend inspection and contract assumptions

The Angular app is a prototype with synchronous mock methods in `src/app/core/services/wealth.service.ts`, typed in `src/app/core/models/wealth.models.ts`. Dashboard, portfolio, stocks, IPO, crypto, profile and settings screens currently use mock data. Watchlists uses `localStorage` for list edits/import, favorites, selected columns and order. Angular allows blank login credentials and README says auth is not integrated.

Contract decisions: backend CRUD uses database numeric IDs (replace sample/custom string watchlist IDs in the Angular model); page indices are zero-based, page size defaults to 15 and is capped at 100; watchlist bulk-add is atomic and duplicate symbols fail with 400; absent market/fundamental/technical data stays null; manual assets must not be assigned fabricated prices. Dashboard and current instrument lookup fixtures are explicitly tagged sample data. Existing UI expects numeric `price` and `change`; treat these values as nullable in the Angular integration model rather than display `0` as a real quote. No auth is implemented: one development workspace is shared, so this service is **development-only** and must not be publicly exposed. Blank login is not interpreted as backend authentication. Add Spring Security and real user ownership before production.

## Endpoints

All paths use `/api/v1`. OpenAPI is available at `/v3/api-docs`; Swagger UI at `/swagger-ui.html`.

| Method | Path | Purpose |
|---|---|---|
| GET | `/dashboard` | Summary, growth history, US/India allocation, portfolio summaries, gainers/losers (`sampleData: true`) |
| GET, POST | `/portfolios` | Paginated list / create (page=0,size=15) |
| GET, PUT, DELETE | `/portfolios/{id}` | Read, replace portfolio and holdings, delete |
| GET | `/watchlists?market=US\|India&page=0&size=15` | Paginated market-specific watchlists |
| POST | `/watchlists` | Create `{name,market}` |
| GET, PATCH, DELETE | `/watchlists/{id}` | Read with paginated items / rename / delete |
| POST | `/watchlists/{id}/items` | Atomic bulk-add `{items:[...]}` |
| PUT, DELETE | `/watchlists/{id}/items/{symbol}` | Update or remove one symbol |
| GET, PUT | `/preferences` | Favorite lists/symbols and ordered visible columns |
| GET | `/instruments?q=apple&market=US` | Sample-only ticker/name lookup |

Watchlist items align with `WatchlistItem`: `symbol`, `name`, nullable `price`, `change`, `trend`, `assetType`, `sector`, `score`, `volume`, `metrics`, plus `sampleData`. Portfolio holdings include `symbol`, `instrument`, `quantity`, `averageCost`, `marketValue`, and `currency`.

Errors have `{timestamp,status,error,message,details}`. Validation errors map field names to messages. Page/sort behavior is deterministic by ID. Duplicate item symbol (case-insensitive) returns HTTP 400; missing resource returns 404.

### Examples

```bash
curl 'http://localhost:8080/api/v1/watchlists?market=US&page=0&size=15'
curl -X POST http://localhost:8080/api/v1/watchlists -H 'Content-Type: application/json' \
  -d '{"name":"Long-term ideas","market":"US"}'
curl -X POST http://localhost:8080/api/v1/watchlists/1/items -H 'Content-Type: application/json' \
  -d '{"items":[{"symbol":"AAPL","name":"Apple Inc.","price":null,"change":null,"trend":null,"assetType":"Stock"},{"symbol":"MSFT","name":"Microsoft","price":null,"change":null}]}'
curl -X PUT http://localhost:8080/api/v1/portfolios -H 'Content-Type: application/json' \
  -d '{"name":"Core account","country":"United States","currency":"USD","assetType":"Stocks & ETFs","holdings":[{"symbol":"AAPL","instrument":"Apple Inc.","quantity":10,"averageCost":150,"marketValue":null,"currency":"USD"}]}'
```

## Prerequisites / run

- JDK 21, Maven 3.9+, PostgreSQL 15+.
- Set `POSTGRES_URL` (JDBC URL), `POSTGRES_USER`, and `POSTGRES_PASSWORD`; defaults are local development convenience only. `FRONTEND_ORIGIN` defaults to `http://localhost:4200`; use an exact origin, not wildcard. `SERVER_PORT` defaults to 8080.
- Create a PostgreSQL database `folio_pro`, then run:

```bash
export POSTGRES_URL=jdbc:postgresql://localhost:5432/folio_pro
export POSTGRES_USER=folio_pro
export POSTGRES_PASSWORD=local-only-password
export FRONTEND_ORIGIN=http://localhost:4200
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn test
```

Flyway applies `src/main/resources/db/migration`. Optional `dev` profile seeds two sample watchlists only when watchlists are empty; it inserts no holdings and never claims live prices. Keep secrets in environment/secret manager; no provider credentials are configured. Market data is behind `SampleMarketDataProvider`, replaceable by a future provider adapter, and demo fixtures are not real recommendations or current quotes.

## Angular integration guide

Replace synchronous `WealthService` mock methods with `HttpClient` methods (configured with a base URL from Angular environment config, no credentials baked into source). Map `getMetricCards/getCountryPortfolios/getPortfolioOverviews/getTopGainers/getTopLosers` to `GET /api/v1/dashboard`; `getWatchlists(market)` to paginated `GET /api/v1/watchlists`; create/rename/delete lists via POST/PATCH/DELETE; staged additions/import CSV can call bulk POST; item edits/removal use PUT/DELETE; symbol suggestions use `/instruments?q=...&market=...`; portfolio CRUD and holdings use `/portfolios`; settings persist to `/preferences`.

Move browser keys `folio-pro.favorite-watchlists`, `folio-pro.favorite-symbols`, and `folio-pro.watchlist-columns` to `GET/PUT /preferences`. Move `folio-pro.watchlist-data` to watchlist/item endpoints. CSV parsing/export can remain client-side, with parsed items sent in a bulk add request. `WatchlistsComponent` needs to consume server pagination (currently it slices in-memory data), accept numeric IDs, and render null prices/change/metrics as unavailable (not zero). Login/register and blank-credential demo entry should remain frontend-only until a separately defined secure authentication/token/ownership contract is implemented.

## Tests and limitations

`mvn test` includes service/controller Spring tests using H2 and validates CRUD, bulk duplicate handling, pagination and request validation. There is intentionally no authentication or production ownership isolation. The current Angular dashboard contains hardcoded illustrative values; this API labels responses sample and does not provide live market feeds, financial advice, user identity or multi-user authorization.
