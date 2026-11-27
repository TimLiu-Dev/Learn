# 1. Split the library into catalogue and lending modules, shipped as one application

Date: 2026-11-26

## Status

Accepted

## Context

The library is one Gradle module in which the lending rules, the book records and the
SQLite code can all see each other. Two kinds of change keep colliding: cataloguing (new
books, ISBNs, search) and lending (loans, limits, fees). Both use the word "book" for
different things. To the catalogue a book is a title with an author and an ISBN; to lending
it is one physical copy that is either on the shelf or out. One shared Book class keeps
growing fields that half the code never uses.

We are one small team. We deploy once, on one machine, with one database. Nothing needs to
scale or be released separately.

## Decision

We will split the code into the Gradle modules `:catalogue` and `:lending`, plus `:platform`
(the event bus) and `:app` (the composition root), built and shipped as one application.

Each module publishes a small API package (`catalogue.api`); everything else in it is
internal. Lending may use only `catalogue.api`. An architecture test in `:app` enforces
this, and also that the lending domain names no catalogue, bus or JDBC class.

We will not split the two into separate services.

## Consequences

Each context gets its own model in its own words: lending's `ShelfCopy` has no author, and
the catalogue's `Book` has no borrower.

Reaching across the boundary fails the build, at compile time in Kotlin (`internal`) and in
the architecture test in both languages.

There is more ceremony: four build files instead of one, and a translation layer in lending.

Both modules still run in one process, so a call between them is fast and can't fail halfway
across a network, and one database transaction can still span them if we ever need it. If
we later need separate deployments, this module boundary is where the service boundary
goes, recorded in a new ADR that supersedes this one.
