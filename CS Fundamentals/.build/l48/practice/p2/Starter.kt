import kotlin.math.sqrt

// P(outage | alert), from P(outage), P(alert | outage) and P(alert | normal).
fun pOutageGivenAlert(prior: Double, hitRate: Double, falseAlarmRate: Double): Double =
    TODO("Bayes' rule, with P(alert) from both ways an alert can happen")

// Standard deviation: the square root of the mean squared distance from the mean (divide by n).
fun stdDev(timings: List<Double>): Double = TODO("find the mean first, then the mean squared distance")
