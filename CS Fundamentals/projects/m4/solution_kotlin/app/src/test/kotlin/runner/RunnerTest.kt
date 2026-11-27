package runner

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Runs the real Runner on virtual time (L29): delays take no real time, and timestamps are exact. */
class RunnerTest {
    private fun TestScope.events(buffer: ByteArrayOutputStream) =
        Events(PrintStream(buffer, true)) { testScheduler.currentTime * 1_000_000 }

    private suspend fun TestScope.run(text: String, opts: Options): Pair<Map<Outcome, Int>, List<String>> {
        val buffer = ByteArrayOutputStream()
        val counts = Runner(Plan.order(JobsFile.parse(text)), opts, events(buffer)).run()
        return counts to buffer.toString().lines().filter { it.isNotEmpty() && !it.contains(" queued ") }
    }

    @Test
    fun `retries with backoff 50 then 100 ms, exactly`() = runTest {
        val (counts, lines) = run("id=a duration=100 fail=2", Options("f", workers = 1, retries = 3))
        assertEquals(
            listOf(
                "t=0000 start a attempt=1", "t=0100 fail a attempt=1", "t=0100 retry a attempt=2 delay=50",
                "t=0150 start a attempt=2", "t=0250 fail a attempt=2", "t=0250 retry a attempt=3 delay=100",
                "t=0350 start a attempt=3", "t=0450 done a attempt=3",
            ),
            lines,
        )
        assertEquals(1, counts[Outcome.DONE])
    }

    @Test
    fun `a timeout cancels the attempt at exactly the limit`() = runTest {
        val (counts, lines) = run("id=slow duration=5000", Options("f", workers = 1, timeoutMs = 150))
        assertEquals(listOf("t=0000 start slow attempt=1", "t=0150 timeout slow attempt=1", "t=0150 giveup slow attempts=1"), lines)
        assertEquals(1, counts[Outcome.GIVEUP])
    }

    @Test
    fun `never runs more than --workers attempts at once`() = runTest {
        val text = (1..9).joinToString("\n") { "id=j$it duration=${it * 10}" }
        val (_, lines) = run(text, Options("f", workers = 3))
        var running = 0
        var peak = 0
        for (line in lines) {
            if (" start " in line) running++
            if (" done " in line) running--
            peak = maxOf(peak, running)
        }
        assertEquals(3, peak)
    }

    @Test
    fun `skips everything downstream of a job that gives up`() = runTest {
        val (counts, lines) = run(
            "id=a duration=10 fail=9\nid=b duration=10 deps=a\nid=c duration=10 deps=b\nid=d duration=10",
            Options("f", workers = 2),
        )
        assertTrue("t=0010 skip b dep=a" in lines, lines.toString())
        assertTrue("t=0010 skip c dep=b" in lines, lines.toString())
        assertEquals(mapOf(Outcome.DONE to 1, Outcome.GIVEUP to 1, Outcome.SKIP to 2, Outcome.CANCEL to 0, Outcome.DROP to 0), counts)
    }
}
