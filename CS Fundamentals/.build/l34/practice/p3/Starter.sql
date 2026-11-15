-- Customers with fewer than 2 paid orders, including customers with none.
-- This draft misses most of them. Fix it.
SELECT c.name, count(*) AS paid_orders
FROM customers c
JOIN orders o ON o.customer_id = c.id
WHERE o.status = 'paid'
GROUP BY c.id
ORDER BY paid_orders, c.name;
