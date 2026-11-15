CREATE TABLE accounts (
  id            INTEGER PRIMARY KEY,
  owner         TEXT,
  balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0)
);
INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000);

CREATE TABLE transfers (
  id      INTEGER PRIMARY KEY,
  from_id INTEGER NOT NULL,
  to_id   INTEGER NOT NULL,
  cents   INTEGER NOT NULL
);

-- Your session so far. The transaction is still open:
BEGIN;
INSERT INTO transfers (from_id, to_id, cents) VALUES (1, 2, 15000);
UPDATE accounts SET balance_cents = balance_cents + 15000 WHERE id = 2;
-- UPDATE accounts SET balance_cents = balance_cents - 15000 WHERE id = 1;
-- Runtime error: CHECK constraint failed: balance_cents >= 0 (19)
