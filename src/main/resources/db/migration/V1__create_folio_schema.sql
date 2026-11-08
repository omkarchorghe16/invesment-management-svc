CREATE TABLE portfolios (
  id BIGSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, country VARCHAR(40) NOT NULL,
  currency VARCHAR(3) NOT NULL, asset_type VARCHAR(160), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE holdings (
  id BIGSERIAL PRIMARY KEY, portfolio_id BIGINT NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
  symbol VARCHAR(20) NOT NULL, instrument_name VARCHAR(160) NOT NULL, quantity NUMERIC(24,8) NOT NULL,
  average_cost NUMERIC(20,6) NOT NULL, market_value NUMERIC(20,6), currency VARCHAR(3) NOT NULL,
  UNIQUE(portfolio_id, symbol)
);
CREATE TABLE watchlists (
  id BIGSERIAL PRIMARY KEY, name VARCHAR(40) NOT NULL, market VARCHAR(10) NOT NULL,
  currency VARCHAR(3) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT watchlist_market_check CHECK (market IN ('US','India'))
);
CREATE TABLE watchlist_items (
  id BIGSERIAL PRIMARY KEY, watchlist_id BIGINT NOT NULL REFERENCES watchlists(id) ON DELETE CASCADE,
  symbol VARCHAR(15) NOT NULL, name VARCHAR(160) NOT NULL, price NUMERIC(20,6), change_percent NUMERIC(12,6),
  trend VARCHAR(12), asset_type VARCHAR(50), sector VARCHAR(100), score NUMERIC(8,3), volume VARCHAR(60), metrics TEXT,
  UNIQUE(watchlist_id, symbol)
);
CREATE TABLE user_preferences (
  id BIGSERIAL PRIMARY KEY, user_key VARCHAR(120) NOT NULL UNIQUE,
  favorite_watchlists TEXT NOT NULL DEFAULT '[]', favorite_symbols TEXT NOT NULL DEFAULT '{"US":[],"India":[]}',
  watchlist_columns TEXT NOT NULL DEFAULT '["symbol","name","assetType","price","change","trend"]'
);
CREATE INDEX idx_watchlists_market ON watchlists(market);
CREATE INDEX idx_watchlist_items_watchlist ON watchlist_items(watchlist_id);
