BEGIN;
CREATE TABLE accounts (
  id            INTEGER PRIMARY KEY,
  owner         TEXT,
  balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0),
  frozen        INTEGER  -- 1 = frozen, 0 = not, NULL = never set
);
INSERT INTO accounts VALUES (1, 'Ada', 10000, NULL), (2, 'Bob', 5000, 0), (3, 'Cy', 2000, 1);

-- A 1.00 fee on every account that isn't frozen.
UPDATE accounts SET balance_cents = balance_cents - 100 WHERE frozen <> 1;
SELECT changes();  -- the update count, as executeUpdate would return it
COMMIT;
