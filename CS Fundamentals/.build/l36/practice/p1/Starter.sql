-- The debit failed, so just carry on and send the 50.00.
COMMIT;
UPDATE accounts SET balance_cents = balance_cents + 5000 WHERE id = 2;
UPDATE accounts SET balance_cents = balance_cents - 5000 WHERE id = 1;
