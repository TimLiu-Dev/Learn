package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import catalogue.api.Catalogue;
import java.time.Clock;
import lending.adapters.CatalogueListener;
import lending.adapters.InMemoryBorrowers;
import lending.adapters.InMemoryShelf;
import lending.domain.CopyId;
import lending.domain.LendResult;
import lending.domain.LendingService;
import lending.domain.MemberId;
import org.junit.jupiter.api.Test;
import platform.EventBus;

// The modules together, through the real bus: the only test that needs all of them.
class WiringTest {
    private final EventBus bus = new EventBus();
    private final Catalogue catalogue = Catalogue.create(bus);
    private final InMemoryShelf shelf = new InMemoryShelf();
    private final LendingService lending = new LendingService(shelf, new InMemoryBorrowers(), Clock.systemUTC());

    WiringTest() { new CatalogueListener(shelf).subscribeTo(bus); }

    @Test
    void aCopyAddedToTheCatalogueCanBeLent() {
        catalogue.addBook("9780441478125", "The Left Hand of Darkness", "Ursula K. Le Guin");
        catalogue.addCopy("9780441478125", "C-1");
        assertInstanceOf(LendResult.Lent.class, lending.lend(new CopyId("C-1"), new MemberId(1)));
    }

    @Test
    void aWithdrawnCopyCantBeLent() {
        catalogue.addBook("9780441478125", "The Left Hand of Darkness", "Ursula K. Le Guin");
        catalogue.addCopy("9780441478125", "C-1");
        catalogue.withdrawCopy("C-1");
        assertEquals(new LendResult.NotLendable(), lending.lend(new CopyId("C-1"), new MemberId(1)));
    }

    @Test
    void lendingHasNeverHeardOfACopyTheCatalogueNeverAdded() {
        assertEquals(new LendResult.UnknownCopy(), lending.lend(new CopyId("C-404"), new MemberId(1)));
    }
}
