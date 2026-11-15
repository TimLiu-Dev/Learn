"use strict";

const { Pool } = require("pg");

function createPgPool() {
  const connectionString =
    process.env.DATABASE_URL || "postgresql://postgres:postgres@localhost:5432/orders";

  return new Pool({
    connectionString,
    max: Number(process.env.PG_POOL_MAX || 20),
    idleTimeoutMillis: Number(process.env.PG_IDLE_TIMEOUT_MS || 30000),
    connectionTimeoutMillis: Number(process.env.PG_CONN_TIMEOUT_MS || 3000)
  });
}

module.exports = { createPgPool };
