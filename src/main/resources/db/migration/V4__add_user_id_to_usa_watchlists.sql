ALTER TABLE watchlists_usa
  ADD COLUMN user_id BIGINT NOT NULL DEFAULT 1;

CREATE INDEX idx_watchlists_usa_user_id ON watchlists_usa(user_id);
