# 2. Lending learns about copies from catalogue events

Date: 2026-11-26

## Status

Accepted

## Context

Lending needs to know which copies exist and which have been withdrawn. ADR 1 makes the
catalogue's classes and storage internal. The options:

1. Lending asks the catalogue (`isLendable(barcode)`) on every loan.
2. Lending reads the catalogue's tables directly.
3. The catalogue publishes events (`CopyAdded`, `CopyWithdrawn`) and lending keeps its own
   list of copies.

Option 2 breaks ADR 1. Option 1 is simple, but every loan then depends on the catalogue,
and the catalogue's API grows with each new thing lending wants to ask.

## Decision

We will use option 3. After each change the catalogue publishes a `CatalogueEvent` on the
in-process `EventBus`. Lending's `CatalogueListener`, the only lending class that knows the
catalogue exists, translates each event into a `ShelfCopy`. Events carry plain values only:
barcode, ISBN text and title.

## Consequences

The catalogue doesn't know lending exists. A new subscriber needs no change to the catalogue.

Lending keeps its own copy of a little catalogue data (the title), which is only as fresh as
the last event.

The flow "add a copy, then it can be lent" is no longer visible in one place in the code.
`WiringTest` documents it.

The bus is in memory and synchronous. If the process dies between a catalogue change and
the delivery of its event, the event is lost. That is acceptable while everything is in
memory. Once the catalogue stores its data in a database, its events must be written in
the same transaction (a transactional outbox); we will revisit this decision then.

The event types are now a published contract. Removing or renaming a field is a breaking
change for lending.
