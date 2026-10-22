package desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import desk.LoanDesk.LendResult;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoanDeskTest {
    private final LoanDesk desk = new LoanDesk();
    private final long dune = desk.addBook("Dune");
    private final long ada = desk.addMember("Ada");
    private final long linus = desk.addMember("Linus");

    @Test void lendingAFreeBookWorks() {
        assertEquals(LendResult.LENT, desk.lend(dune, ada));
        assertEquals("Ada", desk.borrowerOf(dune));
    }

    @Test void aSecondLenderCannotOverwriteTheFirst() {
        desk.lend(dune, ada);
        assertEquals(LendResult.ALREADY_ON_LOAN, desk.lend(dune, linus));
        assertEquals("Ada", desk.borrowerOf(dune));
    }

    @Test void unknownBookAndMemberAreReported() {
        assertEquals(LendResult.NO_SUCH_BOOK, desk.lend(99, ada));
        assertEquals(LendResult.NO_SUCH_MEMBER, desk.lend(dune, 99));
    }

    @Test void givingBackTwiceIsHarmless() {
        desk.lend(dune, ada);
        desk.giveBack(dune);
        desk.giveBack(dune);
        assertNull(desk.borrowerOf(dune));
        assertEquals(LendResult.LENT, desk.lend(dune, linus));
    }

    @Test void thePublicInterfaceIsFiveMethods() {
        List<String> names = Arrays.stream(LoanDesk.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers())).map(m -> m.getName()).sorted().toList();
        assertEquals(List.of("addBook", "addMember", "borrowerOf", "giveBack", "lend"), names);
    }
}
