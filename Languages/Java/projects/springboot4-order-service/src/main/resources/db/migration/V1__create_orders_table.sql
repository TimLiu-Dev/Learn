CREATE SEQUENCE IF NOT EXISTS orders_id_seq START WITH 1000 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT PRIMARY KEY DEFAULT nextval('orders_id_seq'),
    customer_id  VARCHAR(64)  NOT NULL,
    sku          VARCHAR(64)  NOT NULL,
    amount_cents BIGINT       NOT NULL CHECK (amount_cents > 0),
    currency     VARCHAR(3)   NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_created
    ON orders (customer_id, created_at DESC);
