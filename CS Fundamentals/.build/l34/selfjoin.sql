SELECT m1.name, m2.name, substr(m1.joined, 1, 7) AS month
FROM members m1 JOIN members m2
  ON substr(m1.joined, 1, 7) = substr(m2.joined, 1, 7) AND m1.id < m2.id;
