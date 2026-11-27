package app

import catalogue.api.Catalogue
import lending.adapters.CatalogueListener
import lending.adapters.InMemoryBorrowers
import lending.adapters.InMemoryShelf
import lending.domain.CopyId
import lending.domain.LendResult
import lending.domain.LendingService
import lending.domain.MemberId
import platform.EventBus
import java.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

// The modules together, through the real bus: the only test that needs all of them.
class WiringTest {
    private val bus = EventBus()
    private val catalogue = Catalogue.create(bus)
    private val shelf = InMemoryShelf().also { CatalogueListener(it).subscribeTo(bus) }
    private val lending = LendingService(shelf, InMemoryBorrowers(), Clock.systemUTC())

    @Test fun `a copy added to the catalogue can be lent`() {
        catalogue.addBook("9780441478125", "The Left Hand of Darkness", "Ursula K. Le Guin")
        catalogue.addCopy("9780441478125", "C-1")
        assertIs<LendResult.Lent>(lending.lend(CopyId("C-1"), MemberId(1)))
    }

    @Test fun `a withdrawn copy can't be lent`() {
        catalogue.addBook("9780441478125", "The Left Hand of Darkness", "Ursula K. Le Guin")
        catalogue.addCopy("9780441478125", "C-1")
        catalogue.withdrawCopy("C-1")
        assertEquals(LendResult.NotLendable, lending.lend(CopyId("C-1"), MemberId(1)))
    }

    @Test fun `lending has never heard of a copy the catalogue never added`() {
        assertEquals(LendResult.UnknownCopy, lending.lend(CopyId("C-404"), MemberId(1)))
    }
}
