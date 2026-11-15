-- Step 1: add the constraints these two tables are missing.
CREATE TABLE members (
  id     INTEGER PRIMARY KEY,
  name   TEXT NOT NULL,
  email  TEXT
);
CREATE TABLE fines (
  id            INTEGER PRIMARY KEY,
  member_id     INTEGER,
  amount_cents  INTEGER NOT NULL,
  reason        TEXT NOT NULL
);

-- Step 2: copy each member in once (INSERT ... SELECT).

-- Step 3: copy each fine in, keeping its id and pointing at its member.
