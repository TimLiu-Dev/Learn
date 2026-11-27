package app;

import catalogue.api.Catalogue;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import lending.adapters.CatalogueListener;
import lending.adapters.InMemoryBorrowers;
import lending.adapters.InMemoryShelf;
import lending.domain.CopyId;
import lending.domain.LendingService;
import lending.domain.MemberId;
import platform.EventBus;

// The composition root: the one place that knows every module and wires them together.
public class Main {
    public static void main(String[] args) {
        EventBus bus = new EventBus();
        bus.subscribe(Object.class, e -> System.out.println("  event: " + e));   // show every event crossing a boundary
        Catalogue catalogue = Catalogue.create(bus);
        InMemoryShelf shelf = new InMemoryShelf();
        new CatalogueListener(shelf).subscribeTo(bus);
        Clock clock = Clock.fixed(Instant.parse("2026-11-25T10:00:00Z"), ZoneOffset.UTC);
        LendingService lending = new LendingService(shelf, new InMemoryBorrowers(), clock);
        MemberId ada = new MemberId(1);
        MemberId alan = new MemberId(2);

        catalogue.addBook("978-0-441-47812-5", "The Left Hand of Darkness", "Ursula K. Le Guin");
        catalogue.addCopy("978-0-441-47812-5", "C-001");
        catalogue.addCopy("978-0-441-47812-5", "C-002");
        catalogue.addBook("978-0-8070-8369-7", "Kindred", "Octavia E. Butler");
        catalogue.addCopy("978-0-8070-8369-7", "C-003");
        try {
            catalogue.addBook("978-0-8070-8369-8", "Kindred (typo)", "Octavia E. Butler");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }

        System.out.println("Ada borrows C-001:  " + lending.lend(new CopyId("C-001"), ada));
        System.out.println("Alan borrows C-001: " + lending.lend(new CopyId("C-001"), alan));
        catalogue.withdrawCopy("C-002");
        System.out.println("Alan borrows C-002: " + lending.lend(new CopyId("C-002"), alan));
        System.out.println("Alan borrows C-999: " + lending.lend(new CopyId("C-999"), alan));
        System.out.println("search 'le guin':   " + catalogue.search("le guin"));
        System.out.println("Ada returns C-001:  " + lending.giveBack(new CopyId("C-001"), ada));
        System.out.println("Alan borrows C-001: " + lending.lend(new CopyId("C-001"), alan));
    }
}
