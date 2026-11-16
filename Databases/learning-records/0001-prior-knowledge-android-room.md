# Prior Knowledge: Android Room DB Basics

Status: active

The learner is a junior Android developer whose closest prior database experience is basic usage of Android Room (`@Entity`, `@Dao`, `@Query`, `@ForeignKey`, local SQLite persistence, and simple migrations).

## Implications
- Skip introductory "what is a table or SELECT" hand-holding; start directly by bridging Room annotations to database engine invariants, strict typing, and multi-client backend realities.
- Explicitly contrast single-user local SQLite assumptions (one process, local flash, default `PRAGMA foreign_keys = OFF` outside Room, non-`STRICT` dynamic typing) against PostgreSQL and concurrent production servers.
