package bench;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)                       // one instance per benchmark thread
@BenchmarkMode(Mode.AverageTime)           // report time per call...
@OutputTimeUnit(TimeUnit.MICROSECONDS)     // ...in microseconds
public class ConcatBench {
    @Param({"100", "1000"})                // run every benchmark at both sizes
    int n;

    @Benchmark
    public String plusInLoop() {
        String s = "";
        for (int i = 0; i < n; i++) s += i;  // builds a new String every time
        return s;                            // returning it keeps the work alive
    }

    @Benchmark
    public String stringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(i);
        return sb.toString();
    }
}
