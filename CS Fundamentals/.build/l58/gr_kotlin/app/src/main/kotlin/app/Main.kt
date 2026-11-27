package app

import catalogue.api.Catalogue
import lending.adapters.CatalogueListener
import lending.adapters.InMemoryBorrowers
import lending.adapters.InMemoryShelf
import lending.domain.CopyId
import lending.domain.LendingService
import lending.domain.MemberId
import platform.EventBus
import platform.subscribe
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

// The composition root: the one place that knows every module and wires them together.
fun main() {
    val bus = EventBus()
    bus.subscribe<Any> { println("  event: $it") }           // show every event crossing a boundary
    val catalogue = Catalogue.create(bus)
    val shelf = InMemoryShelf()
    CatalogueListener(shelf).subscribeTo(bus)
    val clock = Clock.fixed(Instant.parse("2026-11-25T10:00:00Z"), ZoneOffset.UTC)
    val lending = LendingService(shelf, InMemoryBorrowers(), clock)
    val ada = MemberId(1)
    val alan = MemberId(2)

    catalogue.addBook("978-0-441-47812-5", "The Left Hand of Darkness", "Ursula K. Le Guin")
    catalogue.addCopy("978-0-441-47812-5", "C-001")
    catalogue.addCopy("978-0-441-47812-5", "C-002")
    catalogue.addBook("978-0-8070-8369-7", "Kindred", "Octavia E. Butler")
    catalogue.addCopy("978-0-8070-8369-7", "C-003")
    println(runCatching { catalogue.addBook("978-0-8070-8369-8", "Kindred (typo)", "Octavia E. Butler") }.exceptionOrNull())

    println("Ada borrows C-001:  " + lending.lend(CopyId("C-001"), ada))
    println("Alan borrows C-001: " + lending.lend(CopyId("C-001"), alan))
    catalogue.withdrawCopy("C-002")
    println("Alan borrows C-002: " + lending.lend(CopyId("C-002"), alan))
    println("Alan borrows C-999: " + lending.lend(CopyId("C-999"), alan))
    println("search 'le guin':   " + catalogue.search("le guin"))
    println("Ada returns C-001:  " + lending.giveBack(CopyId("C-001"), ada))
    println("Alan borrows C-001: " + lending.lend(CopyId("C-001"), alan))
}
