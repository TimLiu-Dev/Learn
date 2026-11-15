-- Keep the session, take Bob's 150.00 back by hand, then do the real transfer.
COMMIT;
BEGIN;
UPDATE accounts SET balance_cents = balance_cents - 15000 WHERE id = 2;
INSERT INTO transfers (from_id, to_id, cents) VALUES (1, 2, 5000);
UPDATE accounts SET balance_cents = balance_cents + 5000 WHERE id = 2;
UPDATE accounts SET balance_cents = balance_cents - 5000 WHERE id = 1;
COMMIT;
