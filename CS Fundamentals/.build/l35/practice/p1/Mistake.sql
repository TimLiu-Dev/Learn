CREATE TABLE members (
  id     INTEGER PRIMARY KEY,
  name   TEXT NOT NULL,
  email  TEXT NOT NULL
);
CREATE TABLE fines (
  id            INTEGER PRIMARY KEY,
  member_id     INTEGER NOT NULL REFERENCES members(id),
  amount_cents  INTEGER NOT NULL,
  reason        TEXT NOT NULL
);
INSERT INTO members (name, email)
  SELECT DISTINCT member_name, member_email FROM fines_flat;
INSERT INTO fines (id, member_id, amount_cents, reason)
  SELECT f.fine_id, m.id, f.amount_cents, f.reason
  FROM fines_flat f JOIN members m ON m.email = f.member_email;
