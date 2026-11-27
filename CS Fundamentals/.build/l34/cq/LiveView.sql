CREATE TABLE loans (id INTEGER PRIMARY KEY, returned TEXT);
INSERT INTO loans VALUES (1, NULL), (2, '2026-03-01');
CREATE VIEW open_loans AS SELECT id FROM loans WHERE returned IS NULL;
INSERT INTO loans VALUES (3, NULL), (4, NULL);
UPDATE loans SET returned = '2026-03-09' WHERE id = 1;
SELECT count(*) FROM open_loans;
