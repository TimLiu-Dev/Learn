package bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DeadCodeBench {
    double x = Math.PI;                    // fields, so the JIT can't treat them as constants
    int n = 1000;

    @Benchmark
    public void baseline() { }             // an empty method: the harness's own cost

    @Benchmark
    public void resultIgnored() {
        double sum = 0;
        for (int i = 0; i < n; i++) sum += i * x;
        // sum is never used, so the JIT is allowed to delete the whole loop
    }

    @Benchmark
    public double resultReturned() {
        double sum = 0;
        for (int i = 0; i < n; i++) sum += i * x;
        return sum;                        // JMH consumes the return value
    }

    @Benchmark
    public void resultToBlackhole(Blackhole bh) {
        double sum = 0;
        for (int i = 0; i < n; i++) sum += i * x;
        bh.consume(sum);                   // or hand it to a Blackhole explicitly
    }
}
