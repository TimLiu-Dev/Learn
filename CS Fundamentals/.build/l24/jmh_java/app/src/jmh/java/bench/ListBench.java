package bench;

import org.openjdk.jmh.annotations.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ListBench {
    List<Integer> array = new ArrayList<>();
    List<Integer> linked = new LinkedList<>();

    @Setup                                   // runs before measuring, not timed
    public void fill() {
        for (int i = 0; i < 100_000; i++) {
            array.add(i);
            linked.add(i);
        }
    }

    @Benchmark
    public long sumArrayList() {
        long sum = 0;
        for (int x : array) sum += x;
        return sum;
    }

    @Benchmark
    public long sumLinkedList() {
        long sum = 0;
        for (int x : linked) sum += x;
        return sum;
    }
}
