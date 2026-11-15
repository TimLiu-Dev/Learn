SELECT c.name, count(o.id) AS paid_orders          -- count(o.id) skips the NULL row: 0
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.id
                  AND o.status = 'paid'            -- in ON, so unmatched customers survive
GROUP BY c.id
HAVING count(o.id) < 2                             -- a test on groups, after counting
ORDER BY paid_orders, c.name;
