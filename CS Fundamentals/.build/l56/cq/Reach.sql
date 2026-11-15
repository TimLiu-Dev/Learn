CREATE TABLE friends (a TEXT, b TEXT);
INSERT INTO friends VALUES ('ana', 'ben'), ('ana', 'eve'), ('ben', 'cai'), ('eve', 'cai');

WITH RECURSIVE r(person, hops) AS (
  SELECT 'ana', 0
  UNION ALL
  SELECT f.b, r.hops + 1 FROM r JOIN friends f ON f.a = r.person
)
SELECT count(*), count(DISTINCT person) FROM r;
