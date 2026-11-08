-- prereqs.sql: everything you must take before 'distributed', and how far back it is.
-- Run: /usr/bin/sqlite3 :memory: < prereqs.sql
.mode box
.headers on
CREATE TABLE requires (course TEXT, needs TEXT);
INSERT INTO requires VALUES
  ('distributed', 'networks'), ('distributed', 'databases'), ('networks', 'os'),
  ('databases', 'datastructures'), ('os', 'datastructures'), ('datastructures', 'programming');

WITH RECURSIVE before(course, depth) AS (
  SELECT needs, 1 FROM requires WHERE course = 'distributed'
  UNION
  SELECT r.needs, b.depth + 1 FROM before b JOIN requires r ON r.course = b.course
)
SELECT course, max(depth) AS levels_back FROM before GROUP BY course ORDER BY levels_back DESC, course;
