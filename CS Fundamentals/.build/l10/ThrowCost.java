// How expensive is an exception used as an ordinary "not found" signal?
// Each call goes 20 frames deep, then reports a miss: by return value, or by throwing.
public class ThrowCost {
    static final class Miss extends RuntimeException {}             // records a stack trace
    static final class CheapMiss extends RuntimeException {
        CheapMiss() { super(null, null, false, false); }            // writableStackTrace = false
    }

    static int findOrMinusOne(int depth) { return depth == 0 ? -1 : findOrMinusOne(depth - 1); }
    static int findOrThrow(int depth)    { if (depth == 0) throw new Miss();      return findOrThrow(depth - 1); }
    static int findOrThrowCheap(int depth) { if (depth == 0) throw new CheapMiss(); return findOrThrowCheap(depth - 1); }

    interface Lookup { int run(); }

    static long nanosPerCall(Lookup lookup, int calls) {
        long best = Long.MAX_VALUE, sink = 0;
        for (int round = 0; round < 15; round++) {                  // first rounds are JIT warm-up
            long start = System.nanoTime();
            for (int i = 0; i < calls; i++) {
                try { sink += lookup.run(); } catch (RuntimeException e) { sink--; }
            }
            best = Math.min(best, System.nanoTime() - start);
        }
        if (sink == 42) System.out.println("unlikely");             // keep the work observable
        return best / calls;
    }

    public static void main(String[] args) {
        int calls = 200_000;
        System.out.println("return -1          : " + nanosPerCall(() -> findOrMinusOne(20), calls) + " ns per miss");
        System.out.println("throw (with trace) : " + nanosPerCall(() -> findOrThrow(20), calls) + " ns per miss");
        System.out.println("throw (no trace)   : " + nanosPerCall(() -> findOrThrowCheap(20), calls) + " ns per miss");
    }
}
