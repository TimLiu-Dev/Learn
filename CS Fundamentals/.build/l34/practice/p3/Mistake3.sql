SELECT c.name, count(o.id) AS paid_orders
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.id
WHERE o.status = 'paid'
GROUP BY c.id
HAVING count(o.id) < 2
ORDER BY paid_orders, c.name;
