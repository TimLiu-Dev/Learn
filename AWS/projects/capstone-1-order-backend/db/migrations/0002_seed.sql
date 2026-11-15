-- 0002_seed.sql
-- Idempotent seed SKUs for local verification and load testing.

INSERT INTO inventory (sku, name, available, version)
VALUES
  ('SKU-KB-01', 'Mechanical Keyboard 75%', 50, 1),
  ('SKU-MS-02', 'Wireless Ergonomic Mouse', 100, 1),
  ('SKU-POISON-99', 'Simulated Poison Pill SKU (Triggers Worker DLQ)', 25, 1)
ON CONFLICT (sku) DO NOTHING;
