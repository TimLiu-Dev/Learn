# Database Engineering Resources

## Knowledge

- [PostgreSQL Official Documentation](https://www.postgresql.org/docs/current/)
  Authoritative reference for PostgreSQL SQL syntax, MVCC concurrency control, `EXPLAIN`, partitioning, `VACUUM`, logical replication, and Row-Level Security. Use for: every production Postgres feature and behavior check.
- [SQLite Official Documentation — Datatypes, STRICT Tables & WAL](https://www.sqlite.org/stricttables.html)
  Primary specification for SQLite type affinity, `STRICT` tables, `WITHOUT ROWID`, `PRAGMA foreign_keys`, and WAL mode. Use for: understanding where local SQLite/Room differs from server databases.
- [Course: CMU 15-445/645 Intro to Database Systems — Andy Pavlo](https://15445.courses.cs.cmu.edu/)
  Gold-standard university course covering slotted pages, buffer pools, B+Trees, query execution, locking, and ARIES crash recovery. Use for: Phase 2 & Phase 3 engine internals.
- [Book: *Use The Index, Luke!* — Markus Winand](https://use-the-index-luke.com/)
  Developer-focused guide to B-Tree anatomy, composite index ordering, SARGable predicates, join algorithms, and keyset vs `OFFSET` pagination. Use for: Phase 2 query optimization.
- [Book: *Designing Data-Intensive Applications* — Martin Kleppmann](https://dataintensive.net/)
  Foundational text on storage engines (B-Tree vs LSM-Tree), replication lag, transactions/write skew, partitioning, consensus, and stream/CDC architectures. Use for: Phases 2–5 system design.
- [Book: *Database Internals* — Alex Petrov](https://www.databass.dev/)
  Deep dive into on-disk B-Tree/LSM structures and distributed consensus (Raft/Paxos, distributed transactions). Use for: Lessons 5, 8, and 14.
- [pgvector Official Repository & Docs](https://github.com/pgvector/pgvector)
  Primary source for PostgreSQL vector data types (`vector`, `halfvec`), distance operators (`<->`, `<=>`), HNSW/IVFFlat index tuning, and hybrid search. Use for: Lesson 17 & PulseLedger Capstone.
- [PgBouncer Documentation](https://www.pgbouncer.org/config.html)
  Authoritative reference for transaction vs session pooling modes, `default_pool_size`, `max_client_conn`, and `SET LOCAL` / prepared statement rules. Use for: Lesson 12 & PulseLedger Capstone.
- [Apache Iceberg Table Format Specification](https://iceberg.apache.org/spec/)
  Primary specification for open lakehouse table formats (ACID snapshots, schema/partition evolution, time travel) built over Parquet. Use for: Lesson 16.
- [Redis Official Documentation — Lua Scripting & Data Structures](https://redis.io/docs/latest/develop/interact/programmability/eval-intro/)
  Reference for atomic server-side rate limiters (`EVAL`/`EVALSHA`), Sorted Sets, Streams, and RDB/AOF durability. Use for: Lesson 15.

## Wisdom (Communities)

- [PostgreSQL Mailing Lists (`pgsql-general` / `pgsql-performance`)](https://www.postgresql.org/list/)
  High-signal engineering discussions directly with PostgreSQL core contributors and DBAs. Use for: query plan troubleshooting and production tuning advice.
- [r/PostgreSQL](https://www.reddit.com/r/PostgreSQL/)
  Active practitioner community for schema reviews, migration war stories, and pooling/extension patterns. Use for: sanity-checking real-world tradeoffs.
- [CMU Database Group YouTube / Discord](https://www.youtube.com/@CMUDatabaseGroup)
  Lectures and systems talks from database researchers and practitioners. Use for: deeper systems engineering intuition.
