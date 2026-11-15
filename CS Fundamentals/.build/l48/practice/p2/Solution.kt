import kotlin.math.sqrt

// P(outage | alert), from P(outage), P(alert | outage) and P(alert | normal).
fun pOutageGivenAlert(prior: Double, hitRate: Double, falseAlarmRate: Double): Double {
    val pAlert = hitRate * prior + falseAlarmRate * (1 - prior)   // law of total probability
    return hitRate * prior / pAlert                                // Bayes' rule
}

// Standard deviation: the square root of the mean squared distance from the mean (divide by n).
fun stdDev(timings: List<Double>): Double {
    val mean = timings.average()
    return sqrt(timings.sumOf { (it - mean) * (it - mean) } / timings.size)
}
