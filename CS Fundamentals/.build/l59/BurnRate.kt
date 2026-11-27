// Three days of traffic, one minute at a time: 1,000 requests a minute against a 99.9% SLO.
// Three quiet days of history come first, so even the 3-day window is full from the start.
const val PER_MINUTE = 1000
const val SLO = 0.999
const val BUDGET = 1 - SLO                       // 0.1% of requests may fail
const val DAY = 24 * 60
const val HISTORY = 3 * DAY

fun errorRateAt(m: Int): Double = when (m - HISTORY) {
    in 10 * 60 until 10 * 60 + 10 -> 0.20                  // day 1, 10:00: a bad deploy, rolled back at 10:10
    23 * 60 -> 0.30                                         // day 1, 23:00: a one-minute blip
    in DAY until DAY + 12 * 60 -> 0.008                     // day 2, 00:00-12:00: a slow leak
    else -> 0.0001                                          // normal background errors
}

class Alert(val name: String, val longMin: Int, val shortMin: Int, val burn: Double) {
    var firing = false
}

fun main() {
    val minutes = 3 * DAY
    val errors = LongArray(HISTORY + minutes + 1)  // prefix sums (L50): errors[i] = errors in minutes before i
    var carry = 0.0
    for (m in 0 until HISTORY + minutes) {
        carry += errorRateAt(m) * PER_MINUTE
        val e = carry.toLong(); carry -= e
        errors[m + 1] = errors[m] + e
    }
    // Error rate over the last `w` minutes, ending at minute m.
    fun rate(m: Int, w: Int): Double {
        val from = maxOf(0, m + 1 - w)
        return (errors[m + 1] - errors[from]).toDouble() / ((m + 1 - from) * PER_MINUTE)
    }

    val alerts = listOf(
        Alert("PAGE   1h & 5m  > 14.4x", 60, 5, 14.4),
        Alert("PAGE   6h & 30m > 6x", 360, 30, 6.0),
        Alert("TICKET 3d & 6h  > 1x", 3 * DAY, 360, 1.0),
    )
    var naiveFirings = 0; var naiveOn = false
    fun clock(m: Int) = "day %d %02d:%02d".format((m - HISTORY) / DAY + 1, m % DAY / 60, m % 60)

    for (m in HISTORY until HISTORY + minutes) {
        for (a in alerts) {
            val on = rate(m, a.longMin) > a.burn * BUDGET && rate(m, a.shortMin) > a.burn * BUDGET
            if (on != a.firing) {
                a.firing = on
                val why = if (on) "  (burn rate over %s: %.1f)".format(a.name.substring(7, 9).trim(), rate(m, a.longMin) / BUDGET) else ""
                println("${clock(m)}  ${a.name}  ${if (on) "fires" else "resolves"}$why")
            }
        }
        val naive = rate(m, 5) > 0.01           // "page if the 5-minute error rate is above 1%"
        if (naive && !naiveOn) naiveFirings++
        naiveOn = naive
    }

    val budget30d = 30 * DAY * PER_MINUTE * BUDGET
    fun spent(from: Int, to: Int) = 100 * (errors[to] - errors[from]) / budget30d
    println()
    println("error budget for 30 days: %.0f failed requests".format(budget30d))
    val h = HISTORY
    println("bad deploy  spent %5.1f%% of it".format(spent(h + 600, h + 610)))
    println("blip        spent %5.1f%%".format(spent(h + 1380, h + 1381)))
    println("slow leak   spent %5.1f%%".format(spent(h + DAY, h + DAY + 720)))
    println("all 3 days  spent %5.1f%%".format(spent(h, h + minutes)))
    println("the naive 'error rate > 1% over 5 min' alert fired $naiveFirings times")
}
