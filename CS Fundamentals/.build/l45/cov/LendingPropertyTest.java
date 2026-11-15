package shelf.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import shelf.domain.Loan;
import shelf.store.MemoryBookStore;
import shelf.store.MemoryLoanStore;
import shelf.store.MemoryMemberStore;

// A property-based test without a framework: random operations, and invariants checked after every one.
class LendingPropertyTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void noSequenceOfLendsAndReturnsBreaksTheLendingRules() {
        for (long seed = 0; seed < 1_000; seed++) run(seed);
    }

    private void run(long seed) {
        Random rnd = new Random(seed);                      // same seed, same run: every failure can be replayed
        MemoryBookStore books = new MemoryBookStore();
        MemoryMemberStore members = new MemoryMemberStore();
        MemoryLoanStore loans = new MemoryLoanStore();
        LendingService service = new LendingService(books, members, loans, clock);
        for (int i = 0; i < 6; i++) books.add("Book " + i, "Author", null);              // books 1..6
        for (int i = 0; i < 2; i++) members.add("Member " + i, "m" + i + "@example.com"); // members 1..2
        List<String> steps = new ArrayList<>();

        for (int step = 0; step < 30; step++) {
            if (rnd.nextInt(3) < 2) {                                   // lend twice as often as return
                long book = rnd.nextLong(1, 8);                         // 7 is an unknown book
                long member = rnd.nextLong(1, 4);                       // 3 is an unknown member
                steps.add("lend(" + book + ", " + member + ") -> " + service.lend(book, member).getClass().getSimpleName());
            } else {
                long loan = rnd.nextLong(1, 12);
                steps.add("return(" + loan + ") -> " + service.returnLoan(loan).getClass().getSimpleName());
            }
            // The properties: whatever just happened, these must still be true.
            List<Loan> open = LongStream.rangeClosed(1, 2).boxed()
                .flatMap(m -> loans.forMember(m).stream()).filter(Loan::isOpen).toList();
            String problem = null;
            if (maxPerKey(open, Loan::memberId) > LendingService.MAX_OPEN_LOANS)
                problem = "a member holds more than " + LendingService.MAX_OPEN_LOANS + " open loans";
            else if (maxPerKey(open, Loan::bookId) > 1)
                problem = "a book is on two open loans";
            if (problem != null)
                fail("seed " + seed + ": " + problem + " after " + steps.size() + " steps:\n  " + String.join("\n  ", steps));
        }
    }

    private static long maxPerKey(List<Loan> loans, Function<Loan, Long> key) {
        return loans.stream().collect(Collectors.groupingBy(key, Collectors.counting()))
            .values().stream().mapToLong(Long::longValue).max().orElse(0);
    }
}
