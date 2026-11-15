-- Customers who have never placed an order (of any status).
SELECT name FROM customers
WHERE id NOT IN (SELECT customer_id FROM orders)
ORDER BY name;
