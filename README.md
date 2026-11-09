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
| GET | `/watchlists?page=0&size=15&market=US\|India` | Paginated watchlists with every ticker nested in each watchlist's `items` array; omit market for both |
| POST | `/watchlists` | Create `{name,market,userId?}`; USA `userId` defaults to `1` |
| POST | `/watchlists/import?market=USA&userId=1` | Import a column-oriented CSV (`multipart/form-data`, field `file`); each header is a watchlist name and cells beneath it are symbols. USA imports store the supplied `userId` (default `1`) in `watchlists_usa`; India imports go to `watchlists_india`. Market defaults to `USA`. |
| GET, PATCH, DELETE | `/watchlists/{id}` | Read with paginated items / rename / delete |
| POST | `/watchlists/{id}/items` | Atomic bulk-add `{items:[...]}` |
| PUT, DELETE | `/watchlists/{id}/items/{symbol}` | Update or remove one symbol |
| GET, PUT | `/preferences` | Favorite lists/symbols and ordered visible columns |
| GET | `/instruments?q=apple&market=US` | Sample-only ticker/name lookup |

Watchlist items contain `symbol`, `name`, and `isFavorite` (false by default). These items are only for symbol tracking/favorites; market quote and technical data are not stored in these tables. USA items are stored in `watchlist_items_usa` and reference `watchlists_usa`; India items are stored in `watchlist_items_india` and reference `watchlists_india`. Portfolio holdings include `symbol`, `instrument`, `quantity`, `averageCost`, `marketValue`, and `currency`.

The watchlist-list response paginates watchlists (zero-based `page`, `size` 1–100) while returning all ticker items for every watchlist on that page:

```json
{
  "content": [
    {
      "id": 1,
      "name": "Long-term ideas",
      "market": "US",
      "currency": "USD",
      "userId": 1,
      "items": [
        {"id": 10, "symbol": "AAPL", "name": "Apple Inc.", "isFavorite": true}
      ]
    }
  ],
  "page": 0,
  "size": 15,
  "totalElements": 1,
  "totalPages": 1
}
```

Errors have `{timestamp,status,error,message,details}`. Validation errors map field names to messages. Page/sort behavior is deterministic by ID. Duplicate item symbol (case-insensitive) returns HTTP 400; missing resource returns 404. CSV imports validate the complete file before saving and create all watchlists/items atomically; duplicate names or symbols, malformed CSV, and invalid symbols fail the import.

Watchlists and items are stored in separate USA and India tables: `watchlists_usa`/`watchlist_items_usa` and `watchlists_india`/`watchlist_items_india`. Each items table has a foreign key to its corresponding watchlist table. Flyway migrations move existing rows into the market-specific tables, preserve IDs and item links, remove quote/technical columns, and initialize `is_favorite` to false. The import endpoint accepts `USA` (also `US` for compatibility) or `India`.

USA watchlists have a `user_id` column. Existing USA rows and new rows default to user ID `1`; the create-watchlist request can include `userId`, and CSV import accepts it as a query parameter. India watchlists do not have this column. The ID is currently a development placeholder and is not authenticated or authorization-checked.

### Postman

Import `postman/Folio-Pro-API.postman_collection.json` and `postman/Folio-Pro-Local-and-Dev.postman_environment.json` into Postman. The shared environment contains `localBaseUrl`, `devBaseUrl`, and the active `baseUrl` used by requests. `baseUrl` defaults to `http://localhost:8081`; replace it with `devBaseUrl` when testing a deployed dev API. Update the `devBaseUrl` placeholder with your actual dev server URL first. To run locally with the Spring `dev` profile and its sample data:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

In the collection, the CSV import request uses `multipart/form-data`; choose the CSV file in its `file` form-data field. `importMarket` is `USA` by default; change it to `India` to write the imported lists to `watchlists_india`. Create portfolio/watchlist requests update `portfolioId` and `watchlistId` from their responses. Delete requests are explicitly marked destructive.

### Examples

```bash
curl 'http://localhost:8081/api/v1/watchlists?market=US&page=0&size=15'
curl -X POST http://localhost:8081/api/v1/watchlists -H 'Content-Type: application/json' \
  -d '{"name":"Long-term ideas","market":"US","userId":1}'
curl -X POST 'http://localhost:8081/api/v1/watchlists/import?market=USA&userId=1' \
  -F 'file=@robinhood_watchlists_by_column.csv'
curl -X POST http://localhost:8081/api/v1/watchlists/1/items -H 'Content-Type: application/json' \
  -d '{"items":[{"symbol":"AAPL","name":"Apple Inc.","isFavorite":true},{"symbol":"MSFT","name":"Microsoft","isFavorite":false}]}'
curl -X PUT http://localhost:8081/api/v1/portfolios -H 'Content-Type: application/json' \
  -d '{"name":"Core account","country":"United States","currency":"USD","assetType":"Stocks & ETFs","holdings":[{"symbol":"AAPL","instrument":"Apple Inc.","quantity":10,"averageCost":150,"marketValue":null,"currency":"USD"}]}'
```

## Prerequisites / run

- JDK 21, Maven 3.9+, PostgreSQL 15+.
- Set `POSTGRES_URL` (JDBC URL), `POSTGRES_USER`, and `POSTGRES_PASSWORD`; defaults are local development convenience only. `FRONTEND_ORIGIN` defaults to `http://localhost:4200`; use an exact origin, not wildcard. `SERVER_PORT` defaults to 8081.
- `APP_LOG_LEVEL` controls application package logging and defaults to `INFO`; use `DEBUG` locally
  when investigating application behavior.
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

## Docker Compose

Docker Compose starts PostgreSQL and the API with a persistent database volume. The database is
published only on host loopback port 5433; the API is available at `http://localhost:8081`.

```bash
cp .env.example .env
# Set POSTGRES_PASSWORD to a local-only value in the ignored .env file.
mvn -DskipTests package
docker compose up --build -d
docker compose ps
```

The API container connects to `postgres:5432` over the Compose network. To run Spring Boot on the
host against this Compose database, set `POSTGRES_URL=jdbc:postgresql://localhost:5433/folio_pro`;
otherwise it uses the configured default `localhost:5432`. Stop the containers with
`docker compose down`; use `docker compose down -v` only when you intend to delete the local
database volume. Do not commit `.env` or put secrets in the Docker image.

Application controller/service invocation logs and exceptions are written to standard output.
Follow container logs with `docker compose logs -f folio-pro-api`; set `APP_LOG_LEVEL=DEBUG` in
`.env` to increase application detail. Method arguments and request bodies are intentionally not
logged. Unexpected API exceptions include a stack trace in the server logs while the client receives
the existing sanitized 500 response.

## Optional Docker Desktop Kubernetes

The `k8s/` manifests and `scripts/deploy-local-k8s.sh` provide a local-only Docker Desktop
deployment. Build the image first, create an ignored `application-k8s.env` with database connection
values reachable from Docker Desktop Kubernetes (for a Mac-hosted PostgreSQL instance,
`POSTGRES_URL=jdbc:postgresql://host.docker.internal:5432/folio_pro`), and deploy with:

```bash
mvn -DskipTests package
docker build -t folio-pro-api:local .
# Store POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD, and FRONTEND_ORIGIN in
# application-k8s.env (KEY=value, one per line); this file is ignored by git.
bash scripts/deploy-local-k8s.sh local
```

Set `APP_SECRETS_FILE` to use a different env file. The script refuses to deploy unless the active
Kubernetes context is `docker-desktop`, the image exists locally, and the env file is present. It
creates/updates a Kubernetes Secret from that ignored env file and verifies readiness. No cloud
deployment or registry publishing is configured.

## Engineering conventions

Repository-specific Copilot guidance is in `.github/copilot-instructions.md`, with Java coding,
architecture, Spring Boot, and logging rules under `.github/instructions/`. Reusable workflows for
architecture, JPA, REST APIs, testing, logging, and future provider integrations are under
`.github/skills/`. These instructions supplement the existing implementation and do not introduce
news-scheduler jobs, notification channels, or provider credentials into this application.

## Angular integration guide

Replace synchronous `WealthService` mock methods with `HttpClient` methods (configured with a base URL from Angular environment config, no credentials baked into source). Map `getMetricCards/getCountryPortfolios/getPortfolioOverviews/getTopGainers/getTopLosers` to `GET /api/v1/dashboard`; `getWatchlists(market)` to paginated `GET /api/v1/watchlists`; create/rename/delete lists via POST/PATCH/DELETE; staged additions/import CSV can call bulk POST; item edits/removal use PUT/DELETE; symbol suggestions use `/instruments?q=...&market=...`; portfolio CRUD and holdings use `/portfolios`; settings persist to `/preferences`.

Move browser keys `folio-pro.favorite-watchlists`, `folio-pro.favorite-symbols`, and `folio-pro.watchlist-columns` to `GET/PUT /preferences`. Move `folio-pro.watchlist-data` to watchlist/item endpoints. CSV parsing/export can remain client-side, with parsed items sent in a bulk add request. `WatchlistsComponent` needs to consume server pagination (currently it slices in-memory data), accept numeric IDs, and render null prices/change/metrics as unavailable (not zero). Login/register and blank-credential demo entry should remain frontend-only until a separately defined secure authentication/token/ownership contract is implemented.

## Tests and limitations

`mvn test` includes service/controller Spring tests using H2 and validates CRUD, bulk duplicate handling, pagination and request validation. There is intentionally no authentication or production ownership isolation. The current Angular dashboard contains hardcoded illustrative values; this API labels responses sample and does not provide live market feeds, financial advice, user identity or multi-user authorization.
