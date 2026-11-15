SELECT 'account' AS kind, owner AS who, balance_cents AS cents FROM accounts
UNION ALL
SELECT 'transfer', from_id || ' -> ' || to_id, cents FROM transfers
ORDER BY 1, 2, 3;
