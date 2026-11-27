CREATE TABLE members (id INTEGER PRIMARY KEY, name TEXT, full_name TEXT);
INSERT INTO members (name) VALUES ('Ada'), ('Grace'), ('Linus'), ('Barbara'), ('Edsger');
-- batch 1
UPDATE members SET full_name = name
  WHERE id IN (SELECT id FROM members WHERE full_name IS NULL LIMIT 2);
-- batch 2
UPDATE members SET full_name = name
  WHERE id IN (SELECT id FROM members WHERE full_name = NULL LIMIT 2);
SELECT count(*) FROM members WHERE full_name IS NULL;
