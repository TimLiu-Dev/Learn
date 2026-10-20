package weather

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.Random

data class Reading(val city: String, val tempC: Int)

sealed interface Outcome {
    data class Ok(val reading: Reading) : Outcome
    data class Failed(val city: String, val reason: String) : Outcome
}

data class Progress(val done: Int, val total: Int, val last: Outcome)

fun interface WeatherApi {
    suspend fun fetch(city: String): Reading
}

// Simulated network: 100-500 ms per city, ~10% failures.
// Each city gets its own seeded Random, so runs are repeatable.
class FakeApi(private val seed: Int = 22, private val minMs: Long = 100,
              private val maxMs: Long = 500) : WeatherApi {
    override suspend fun fetch(city: String): Reading {
        val rnd = Random((city.hashCode() xor seed).toLong())
        delay(rnd.nextLong(minMs, maxMs + 1))
        if (rnd.nextInt(10) == 0) throw IOException("HTTP 503")
        return Reading(city, rnd.nextInt(-5, 36))
    }
}

suspend fun fetchOne(api: WeatherApi, city: String, timeoutMs: Long): Outcome =
    try {
        Outcome.Ok(withTimeout(timeoutMs) { api.fetch(city) })
    } catch (e: TimeoutCancellationException) {
        Outcome.Failed(city, "timeout")
    } catch (e: IOException) {
        Outcome.Failed(city, e.message ?: "I/O error")
    }

fun dashboard(api: WeatherApi, cities: List<String>, timeoutMs: Long = 300): Flow<Progress> =
    channelFlow {
        supervisorScope {                          // one failure never cancels the rest
            cities.map { city ->
                async { fetchOne(api, city, timeoutMs).also { send(it) } }
            }.awaitAll()
        }
    }.withIndex().map { (i, outcome) -> Progress(i + 1, cities.size, outcome) }

fun render(o: Outcome) = when (o) {
    is Outcome.Ok -> "${o.reading.city.padEnd(10)} ${o.reading.tempC} °C"
    is Outcome.Failed -> "${o.city.padEnd(10)} FAILED (${o.reason})"
}

val CITIES = listOf("Austin", "Berlin", "Cairo", "Delhi", "Dublin", "Hanoi", "Lagos",
    "Lima", "London", "Madrid", "Nairobi", "Oslo", "Paris", "Quito", "Rome",
    "Seoul", "Sydney", "Tokyo", "Toronto", "Vienna")

suspend fun run(label: String, api: WeatherApi, timeoutMs: Long) {
    println("== $label")
    val t0 = System.nanoTime()
    var seen = 0
    val finished = withTimeoutOrNull(2000) {       // the user presses q after 2 s
        dashboard(api, CITIES, timeoutMs).collect { p ->
            seen = p.done
            println("[%2d/%d] %s".format(p.done, p.total, render(p.last)))
        }
    }
    val ms = (System.nanoTime() - t0) / 1_000_000
    println(if (finished == null) "q pressed: cancelled after $seen/${CITIES.size}, $ms ms"
            else "all ${CITIES.size} done in $ms ms")
}

fun main() = runBlocking {
    run("normal day", FakeApi(), timeoutMs = 300)
    run("slow network, user gives up", FakeApi(minMs = 1500, maxMs = 4000), timeoutMs = 5000)
}
