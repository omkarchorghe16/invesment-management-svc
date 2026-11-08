CREATE TABLE watchlists_usa (
  id BIGINT PRIMARY KEY DEFAULT nextval('watchlists_id_seq'),
  name VARCHAR(40) NOT NULL,
  market VARCHAR(10) NOT NULL DEFAULT 'US' CHECK (market = 'US'),
  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE watchlists_india (
  id BIGINT PRIMARY KEY DEFAULT nextval('watchlists_id_seq'),
  name VARCHAR(40) NOT NULL,
  market VARCHAR(10) NOT NULL DEFAULT 'India' CHECK (market = 'India'),
  currency VARCHAR(3) NOT NULL DEFAULT 'INR',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO watchlists_usa (id, name, market, currency, created_at)
SELECT id, name, market, currency, created_at
FROM watchlists
WHERE market = 'US';

INSERT INTO watchlists_india (id, name, market, currency, created_at)
SELECT id, name, market, currency, created_at
FROM watchlists
WHERE market = 'India';

ALTER TABLE watchlist_items
  DROP CONSTRAINT IF EXISTS watchlist_items_watchlist_id_fkey;

SELECT setval(
  'watchlists_id_seq',
  GREATEST(COALESCE(MAX(id), 0), 1),
  COALESCE(MAX(id), 0) > 0
)
FROM (
  SELECT id FROM watchlists_usa
  UNION ALL
  SELECT id FROM watchlists_india
) migrated_watchlists;

ALTER SEQUENCE watchlists_id_seq OWNED BY NONE;

DROP TABLE watchlists CASCADE;
