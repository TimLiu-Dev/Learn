# Object-Oriented Design & Low-Level Design Resources

## Knowledge

- [Book: _Patterns of Enterprise Application Architecture_ — Martin Fowler](https://martinfowler.com/books/eaa.html)
  Foundational text on enterprise domain logic patterns. Use for: Rich Domain Model, Value Objects, Unit of Work, Plugin, and Strategy Registry patterns.
- [Book: _Domain-Driven Design: Tackling Complexity in the Heart of Software_ — Eric Evans](https://www.domainlanguage.com/ddd/)
  Canonical reference on domain modeling and tactical patterns. Use for: Aggregate Roots, Entities vs Value Objects, Domain Events, and bounded contexts.
- [Book: _Java Concurrency in Practice_ — Brian Goetz et al.](https://jcip.net/)
  The industry gold-standard on thread-safe object design. Use for: race condition eradication, ReadWriteLock semantics, atomic CAS operations, and lock hierarchies.
- [Book: _Designing Data-Intensive Applications_ — Martin Kleppmann](https://dataintensive.net/)
  Essential bridge between in-memory design and distributed systems. Use for: Optimistic concurrency control, version stamping, and Transactional Outbox.
- [Book: _Microservices Patterns_ — Chris Richardson](https://microservices.io/book)
  Production blueprints for modern distributed systems. Use for: Saga Orchestrator pattern, reverse compensations, and aggregate boundary definition.
- [Paper / Tech Blog: "Designing robust APIs with idempotency" — Stripe Engineering](https://stripe.com/blog/idempotency)
  Practical industry architecture for safe API retries and state synchronization. Use for: Idempotency keys and deduplication caches.
- [Blog: "Anemic Domain Model" — Martin Fowler](https://martinfowler.com/bliki/AnemicDomainModel.html)
  Classic critique of getter/setter data bag anti-patterns. Use for: Understanding why anemic models are rejected in senior interviews.

## Wisdom (Communities)

- [r/softwarearchitecture](https://reddit.com/r/softwarearchitecture)
  High-signal community focused on enterprise architecture and domain design trade-offs. Use for: Reviewing domain boundary decisions and pattern debates.
- [LeetCode Discuss (System Design & OOD tag)](https://leetcode.com/discuss/interview-question?currentPage=1&orderBy=hot&query=&tag=system-design)
  Real-world interview reports from recent FAANG/MAMAA and mid-tech rounds. Use for: Tracking emerging question variations and interviewer rubrics.
- [StaffEng & LeadDev Conferences / Podcasts](https://staffeng.com/)
  Discussions on senior technical leadership and engineering standards. Use for: Aligning verbal interview scripts with executive hiring bar.
