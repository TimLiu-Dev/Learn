-- 2. Each member's loan count, including members with none
SELECT m.name, count(l.id) AS loans
FROM members m LEFT JOIN loans l ON l.member_id = m.id
GROUP BY m.id
ORDER BY loans DESC, m.name;
