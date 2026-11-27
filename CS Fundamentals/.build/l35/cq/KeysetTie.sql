CREATE TABLE loans (id INTEGER PRIMARY KEY, loaned TEXT);
INSERT INTO loans VALUES (1, '2026-01-01'), (2, '2026-01-02'), (3, '2026-01-02'), (4, '2026-01-02'), (5, '2026-01-03');
-- page 1, newest first, 2 per page, was ids 5 and 4; it ended at ('2026-01-02', 4)
SELECT group_concat(id) FROM (SELECT id FROM loans WHERE loaned < '2026-01-02'
                              ORDER BY loaned DESC, id DESC LIMIT 2);
SELECT group_concat(id) FROM (SELECT id FROM loans WHERE (loaned, id) < ('2026-01-02', 4)
                              ORDER BY loaned DESC, id DESC LIMIT 2);
