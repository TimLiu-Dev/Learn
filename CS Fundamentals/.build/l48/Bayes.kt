import kotlin.math.roundToLong
import kotlin.random.Random

// Bayes' rule: P(outage | alert) = P(alert | outage) * P(outage) / P(alert),
// where P(alert) adds up both ways an alert can happen (the law of total probability).
fun pOutageGivenAlert(prior: Double, hitRate: Double, falseAlarmRate: Double): Double {
    val pAlert = hitRate * prior + falseAlarmRate * (1 - prior)
    return hitRate * prior / pAlert
}

fun main() {
    val prior = 0.001          // P(outage): 1 minute in 1,000
    val hitRate = 0.99         // P(alert | outage)
    val falseAlarm = 0.01      // P(alert | normal minute)

    println("P(outage | alert) = %.4f".format(pOutageGivenAlert(prior, hitRate, falseAlarm)))

    // Natural frequencies: the same calculation, counted out over 100,000 minutes
    val outageMinutes = 100_000 * prior                  // 100
    val realAlerts = outageMinutes * hitRate             // 99
    val falseAlerts = (100_000 - outageMinutes) * falseAlarm   // 999
    println("per 100,000 minutes: %.0f real + %.0f false alerts, so %.0f / %.0f = %.4f"
        .format(realAlerts, falseAlerts, realAlerts, realAlerts + falseAlerts, realAlerts / (realAlerts + falseAlerts)))

    // Simulate ten million minutes and simply count
    val rnd = Random(48)
    var alerts = 0
    var alertsDuringOutage = 0
    repeat(10_000_000) {
        val outage = rnd.nextDouble() < prior
        val alert = rnd.nextDouble() < (if (outage) hitRate else falseAlarm)
        if (alert) {
            alerts++
            if (outage) alertsDuringOutage++
        }
    }
    println("simulated: %,d of %,d alerts were real = %.4f"
        .format(alertsDuringOutage, alerts, alertsDuringOutage.toDouble() / alerts))

    // Independent would mean P(outage and alert) == P(outage) * P(alert)
    val pAlert = hitRate * prior + falseAlarm * (1 - prior)
    println("P(outage and alert) = %.6f   P(outage) * P(alert) = %.6f".format(hitRate * prior, prior * pAlert))

    println("prior         hit    false alarm   P(outage | alert)")
    for ((p, hit, fa) in listOf(
        Triple(0.0001, 0.99, 0.01), Triple(0.001, 0.99, 0.01), Triple(0.01, 0.99, 0.01),
        Triple(0.001, 0.999, 0.01), Triple(0.001, 0.99, 0.001))) {
        println("1 in %-7s  %-5s  %-11s   %.4f".format("%,d".format((1 / p).roundToLong()), hit, fa, pOutageGivenAlert(p, hit, fa)))
    }

    // A Bloom filter never misses a present key, wrongly says "maybe" for 2% of absent keys,
    // and only 1% of the keys we look up are really present.
    println("Bloom filter: P(present | maybe) = %.4f".format(pOutageGivenAlert(0.01, 1.0, 0.02)))
}
