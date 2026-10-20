package bench

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
open class ConcatBench {
    @Param("100", "1000")
    var n = 0

    @Benchmark
    fun plusInLoop(): String {
        var s = ""
        for (i in 0 until n) s += i
        return s
    }

    @Benchmark
    fun stringBuilder(): String {
        val sb = StringBuilder()
        for (i in 0 until n) sb.append(i)
        return sb.toString()
    }
}
