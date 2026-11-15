-- A small web shop. Guests can check out without an account,
-- so a guest order has customer_id NULL.
CREATE TABLE customers (
  id   INTEGER PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE orders (
  id          INTEGER PRIMARY KEY,
  customer_id INTEGER REFERENCES customers(id),   -- NULL = guest checkout
  status      TEXT NOT NULL CHECK (status IN ('paid', 'cancelled')),
  total_cents INTEGER NOT NULL
);

INSERT INTO customers (id, name) VALUES
  (1, 'Ana'), (2, 'Ben'), (3, 'Chen'), (4, 'Dara'), (5, 'Eli'), (6, 'Fay');

INSERT INTO orders (id, customer_id, status, total_cents) VALUES
  (1, 1,    'paid',      2500),
  (2, 1,    'paid',      1200),
  (3, 1,    'cancelled',  900),
  (4, 2,    'paid',      4000),
  (5, 3,    'cancelled', 1500),
  (6, 4,    'paid',       800),
  (7, 4,    'paid',      3100),
  (8, NULL, 'paid',      2200),
  (9, NULL, 'cancelled', 1000);
