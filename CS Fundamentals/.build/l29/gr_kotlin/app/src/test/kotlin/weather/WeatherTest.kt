package weather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)    // currentTime is experimental
class WeatherTest {
    // A scripted API: no randomness, exact delays
    private val api = WeatherApi { city ->
        when (city) {
            "Slow" -> { delay(10_000); Reading(city, 0) }
            "Bad" -> { delay(50); throw IOException("HTTP 503") }
            else -> { delay(100); Reading(city, 20) }
        }
    }

    @Test
    fun `slow city times out, bad city fails, the rest succeed`() = runTest {
        val progress = dashboard(api, listOf("A", "Slow", "Bad", "B"), timeoutMs = 300).toList()
        val outcomes = progress.map { it.last }.associateBy {
            when (it) { is Outcome.Ok -> it.reading.city; is Outcome.Failed -> it.city }
        }
        assertEquals(Outcome.Failed("Slow", "timeout"), outcomes["Slow"])
        assertEquals(Outcome.Failed("Bad", "HTTP 503"), outcomes["Bad"])
        assertEquals(Outcome.Ok(Reading("A", 20)), outcomes["A"])
        assertEquals(listOf(1, 2, 3, 4), progress.map { it.done })
        assertEquals(300, currentTime)          // virtual time: all ran concurrently
    }

    @Test
    fun `pressing q cancels everything at 2 seconds`() = runTest {
        val slowApi = WeatherApi { city -> delay(5_000); Reading(city, 1) }
        var seen = 0
        val result = withTimeoutOrNull(2000) {
            dashboard(slowApi, CITIES, timeoutMs = 10_000).collect { seen++ }
        }
        assertNull(result)
        assertEquals(0, seen)
        assertEquals(2000, currentTime)
    }
}
