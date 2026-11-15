import kotlin.math.sqrt

// The base-rate fallacy: compares the two rates and forgets how rare outages are.
fun pOutageGivenAlert(prior: Double, hitRate: Double, falseAlarmRate: Double): Double =
    hitRate / (hitRate + falseAlarmRate)

fun stdDev(timings: List<Double>): Double {
    val mean = timings.average()
    return sqrt(timings.sumOf { (it - mean) * (it - mean) } / timings.size)
}
