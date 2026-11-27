import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Leaves {
    static final AtomicInteger leaves = new AtomicInteger();

    static class Sum extends RecursiveTask<Long> {
        final int lo, hi;
        Sum(int lo, int hi) { this.lo = lo; this.hi = hi; }
        @Override protected Long compute() {
            if (hi - lo <= 4) {                       // at most 4 numbers: add them up
                leaves.incrementAndGet();
                long s = 0;
                for (int i = lo; i < hi; i++) s += i;
                return s;
            }
            int mid = (lo + hi) >>> 1;
            Sum left = new Sum(lo, mid);
            left.fork();
            return new Sum(mid, hi).compute() + left.join();
        }
    }

    public static void main(String[] args) {
        long total = ForkJoinPool.commonPool().invoke(new Sum(1, 21));   // 1..20
        System.out.println(total + " in " + leaves.get() + " leaves");
    }
}
