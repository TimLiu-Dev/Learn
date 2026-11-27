import java.util.Objects;

public class Practice {
    static final long MB = 1024L * 1024;
    static final long SMALL_HEAP = 130_862_280L;  // HotSpot's built-in MaxHeapSize default, about 125 MB
    static final long ALIGN = 8 * MB;             // every heap size this JVM picked was a multiple of 8 MB

    // Round n up to the next multiple of step (n itself if it already is one).
    static long roundUp(long n, long step) {
        throw new UnsupportedOperationException("TODO: round up to a multiple of step");
    }

    // The maximum heap, in bytes, for a memory limit in bytes. xmx may be null.
    static long defaultMaxHeap(long limit, Long xmx, double maxRamPercentage) {
        throw new UnsupportedOperationException("TODO: -Xmx wins; else the small-memory rule or the percentage; then roundUp");
    }

    // CHECKS
    static long heapMb(long limitMb, Long xmxMb, double pct) {
        return defaultMaxHeap(limitMb * MB, xmxMb == null ? null : xmxMb * MB, pct) / MB;
    }

    public static void main(String[] args) {
        check("roundUp lifts 150 MB to 152 MB", 152 * MB, () -> roundUp(150 * MB, ALIGN));
        check("1 GB limit", 256L, () -> heapMb(1024, null, 25));
        check("3000 MB limit: 750 MB, rounded up", 752L, () -> heapMb(3000, null, 25));
        check("128 MB limit", 64L, () -> heapMb(128, null, 25));
        check("400 MB limit: 25% would be only 100 MB", 128L, () -> heapMb(400, null, 25));
        check("2 GB limit, 75%", 1536L, () -> heapMb(2048, null, 75));
        check("2 GB limit, 75%, -Xmx300m", 304L, () -> heapMb(2048, 300L, 75));
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
