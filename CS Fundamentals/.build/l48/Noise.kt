import kotlin.math.sqrt

fun mean(xs: List<Double>): Double = xs.sum() / xs.size

// Var[X] = E[(X - E[X])²], the mean square distance from the mean
fun variance(xs: List<Double>): Double {
    val m = mean(xs)
    return xs.sumOf { (it - m) * (it - m) } / xs.size
}

fun sd(xs: List<Double>): Double = sqrt(variance(xs))

fun main() {
    // Two batches of ten timings (ms) of the SAME code, sorting 1,000,000 ints, captured in one real run
    val a = listOf(42.6, 42.1, 42.3, 45.2, 42.5, 40.5, 39.9, 41.3, 41.7, 41.9)
    val b = listOf(41.7, 41.8, 41.4, 41.0, 41.4, 41.2, 41.3, 41.1, 41.3, 41.5)
    for ((name, xs) in listOf("A" to a, "B" to b)) {
        val shortcut = mean(xs.map { it * it }) - mean(xs) * mean(xs)     // E[X²] - E[X]²
        val sampleSd = sqrt(variance(xs) * xs.size / (xs.size - 1))       // divides by n - 1 instead
        println("%s: mean %.2f  variance %.3f (shortcut %.3f)  sd %.2f  (n-1 version %.2f)"
            .format(name, mean(xs), variance(xs), shortcut, sd(xs), sampleSd))
    }
    val gap = mean(a) - mean(b)
    val gapSd = sqrt(variance(a) / a.size + variance(b) / b.size)        // variances of independent things add
    println("gap of the means %.2f ms, noise in that gap (sd) %.2f ms".format(gap, gapSd))

    // Averaging n independent runs divides the sd by sqrt(n). Model: 42 ms plus noise with sd 1.0 ms.
    val rnd = java.util.Random(16)
    println("runs averaged   sd of the average   1.0 / sqrt(n)")
    for (n in listOf(1, 4, 16, 64)) {
        val averages = List(20_000) { (1..n).sumOf { 42.0 + rnd.nextGaussian() } / n }
        println("%-14d  %-18.3f  %.3f".format(n, sd(averages), 1 / sqrt(n.toDouble())))
    }
}
