-- sizes.sql: how big and how deep are the B-trees? (dbstat is compiled into macOS sqlite3)
CREATE INDEX idx_loans_member ON loans(member_id);
CREATE INDEX idx_loans_book   ON loans(book_id);
CREATE INDEX idx_loans_loaned ON loans(loaned);
.headers on
.mode column
SELECT name, count(*) AS pages, round(sum(pgsize) / 1e6, 2) AS mb,
       max(length(path) - length(replace(path, '/', ''))) AS levels
FROM dbstat WHERE name LIKE '%loans%' GROUP BY name ORDER BY pages DESC;
SELECT count(*) AS root_children FROM dbstat
WHERE name = 'idx_loans_member' AND path GLOB '/[0-9a-f][0-9a-f][0-9a-f]/';
SELECT sum(ncell) AS leaf_entries, count(*) AS leaf_pages,
       sum(ncell) / count(*) AS per_leaf
FROM dbstat WHERE name = 'idx_loans_member' AND pagetype = 'leaf';
