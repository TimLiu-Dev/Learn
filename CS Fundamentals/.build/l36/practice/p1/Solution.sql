-- Throw away everything since BEGIN: the log row and Bob's credit.
ROLLBACK;

-- Then the real transfer, as one all-or-nothing unit.
BEGIN;
INSERT INTO transfers (from_id, to_id, cents) VALUES (1, 2, 5000);
UPDATE accounts SET balance_cents = balance_cents + 5000 WHERE id = 2;
UPDATE accounts SET balance_cents = balance_cents - 5000 WHERE id = 1;
COMMIT;
