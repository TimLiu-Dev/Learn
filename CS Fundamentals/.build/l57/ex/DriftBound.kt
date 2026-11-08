// Exercise: how far apart can two NTP-synced clocks get?
// Clock A runs 50 ppm fast, clock B 50 ppm slow; both are reset to true time every syncSec seconds.

fun worstSkewMs(syncSec: Int, hours: Int = 2, ppm: Double = 50.0): Double {
    var a = 0.0; var b = 0.0                          // each clock's error, in ms
    var worst = 0.0
    for (ms in 1..hours * 3_600_000) {
        a += ppm / 1_000_000                          // 50 ppm = 0.00005 ms gained per real ms
        b -= ppm / 1_000_000
        worst = maxOf(worst, a - b)
        if (ms % (syncSec * 1_000) == 0) { a = 0.0; b = 0.0 }   // sync: both back to true time
    }
    return worst
}

fun main() {
    println("%-10s %12s %12s".format("sync every", "simulated", "2 x ppm x T"))
    for (s in listOf(16, 64, 1024)) {
        println("%-10s %9.1f ms %9.1f ms".format("$s s", worstSkewMs(s), 2 * 50e-6 * s * 1000))
    }
}
