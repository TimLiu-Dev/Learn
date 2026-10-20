package weather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

suspend fun slowGreeting(): String {
    delay(10_000)                                  // ten seconds
    return "hello"
}

@OptIn(ExperimentalCoroutinesApi::class)          // currentTime, advanceTimeBy
class VirtualTimeTest {
    @Test
    fun `a ten second delay takes no real time`() = runTest {
        val start = System.nanoTime()
        assertEquals("hello", slowGreeting())
        assertEquals(10_000, currentTime)          // virtual milliseconds
        val realMs = (System.nanoTime() - start) / 1_000_000
        println("virtual $currentTime ms took $realMs ms of real time")
    }

    @Test
    fun `advanceTimeBy moves the virtual clock`() = runTest {
        var ticks = 0
        val ticker = launch { repeat(5) { delay(1_000); ticks++ } }
        advanceTimeBy(2_500)
        assertEquals(2, ticks)                     // ticks at 1000 and 2000 ms
        ticker.cancel()
    }
}
