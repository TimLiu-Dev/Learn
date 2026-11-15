-- Two inserts the schema should refuse (OR IGNORE skips a row that breaks UNIQUE or NOT NULL).
INSERT OR IGNORE INTO members (name, email) VALUES ('Ada again', 'ada@example.org');
INSERT OR IGNORE INTO members (name, email) VALUES ('No email', NULL);
SELECT '1. members' AS test, count(*) AS result FROM members
UNION ALL SELECT '2. fines', count(*) FROM fines
UNION ALL SELECT '3. original rows missing when joined back', count(*) FROM (
  SELECT fine_id, member_email, member_name, amount_cents, reason FROM fines_flat
  EXCEPT
  SELECT f.id, m.email, m.name, f.amount_cents, f.reason
  FROM fines f JOIN members m ON m.id = f.member_id)
UNION ALL SELECT '4. bad members that got in', count(*) FROM members WHERE name IN ('Ada again', 'No email')
UNION ALL SELECT '5. foreign keys from fines to members', count(*) FROM pragma_foreign_key_list('fines') WHERE "table" = 'members'
ORDER BY 1;
