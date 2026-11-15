import java.util.Map;
import java.util.Objects;

public class Practice {
    static final int PAGE_BITS = 14;                 // 2^14 = 16384 bytes, this Mac's page size
    static final long PAGE_SIZE = 1L << PAGE_BITS;

    // Which virtual page holds this address?
    static long pageNumber(long va) {
        throw new UnsupportedOperationException("TODO: shift the offset bits away");
    }

    // Where inside its page is this address?
    static long offset(long va) {
        throw new UnsupportedOperationException("TODO: keep only the low PAGE_BITS bits");
    }

    // pageTable maps a virtual page number to a physical frame number.
    static Long translate(long va, Map<Long, Long> pageTable) {
        throw new UnsupportedOperationException("TODO: frame * PAGE_SIZE + offset, or null if unmapped");
    }

    // CHECKS
    public static void main(String[] args) {
        check("the last byte of page 0", "0 16383", () -> pageNumber(16383) + " " + offset(16383));
        check("the first byte of page 1", "1 0", () -> pageNumber(16384) + " " + offset(16384));
        check("the JVM heap address splits cleanly", 0x1F0000L, () -> pageNumber(0x7c0000000L));
        check("translation keeps the offset and swaps the page", 7 * PAGE_SIZE + 100, () -> translate(2 * PAGE_SIZE + 100, Map.of(2L, 7L)));
        check("two processes, same virtual address, different RAM", (5 * PAGE_SIZE + 123) + " " + (9 * PAGE_SIZE + 123), () -> {
            long va = 0x7c0000000L + 123;
            return translate(va, Map.of(pageNumber(va), 5L)) + " " + translate(va, Map.of(pageNumber(va), 9L));
        });
        check("an unmapped page is a page fault", null, () -> translate(3 * PAGE_SIZE, Map.of(2L, 7L)));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
