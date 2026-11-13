# Mission split: Part 1 junior-to-mid, Part 2 senior; Kotlin + Spring Boot

**Status:** supersedes the scope in LR-0002 (senior-only, four distributed pillars).

The learner is a junior Android developer starting backend from scratch. After auditing roadmap.sh/system-design (132 nodes; ~45% System Design Primer, ~40% Azure patterns/antipatterns, no hands-on, missing transactions/estimation/consistent hashing/real-time/server-side auth), the mission was split into two sequential 2027 goals: pass L3/L4 backend system design interviews, then pass senior ones. Stack chosen: Kotlin + Spring Boot as primary, Java + Spring Boot on the side.

**Implications**
- A new Phase 0 ("build it") precedes the existing curriculum. Lessons start at 0019.
- Existing lessons 0002, 0003, 0004, 0007 move to Part 2. The rest are reused across Part 1 Phases 1–2.
- The former constraint "defer generic CRUD APIs" is reversed — CRUD is the foundation.
- Leverage Android prior knowledge: Retrofit/OkHttp, JSON, pagination, FCM, offline sync. Teach the server side as the mirror image of what they already consume. (Claimed background, not yet demonstrated — verify in lessons.)
