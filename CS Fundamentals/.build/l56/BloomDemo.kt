// Measure a Bloom filter's false-positive rate and compare it with the formula (1 - e^(-kn/m))^k.
fun main() {
    val n = 100_000
    val probes = 1_000_000
    println("bits/key   k   memory    measured   predicted")
    for ((bitsPerKey, kOverride) in listOf(4 to 0, 8 to 0, 10 to 0, 10 to 1, 16 to 0)) {
        val m = n * bitsPerKey
        val k = if (kOverride > 0) kOverride else Bloom.optimalK(m, n)
        val bloom = Bloom(m, k)
        for (i in 0 until n) bloom.add("key-$i")
        check((0 until n).all { bloom.mightContain("key-$it") })            // never a false "no"
        val fp = (0 until probes).count { bloom.mightContain("absent-$it") }
        println("%8d  %2d  %5d KB    %6.3f%%    %6.3f%%".format(
            bitsPerKey, k, m / 8 / 1024, 100.0 * fp / probes, 100 * Bloom.predicted(m, n, k)))
    }
}
