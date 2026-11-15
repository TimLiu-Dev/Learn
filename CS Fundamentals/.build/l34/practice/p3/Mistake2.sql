SELECT c.name, count(*) AS paid_orders
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.id
                  AND o.status = 'paid'
GROUP BY c.id
HAVING count(*) < 2
ORDER BY paid_orders, c.name;
