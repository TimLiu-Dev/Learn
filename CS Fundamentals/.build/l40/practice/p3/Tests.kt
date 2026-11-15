import org.junit.Assert.assertEquals
import org.junit.Test

class RunJobTest {
    @Test fun `a job whose steps all exit 0 passes`() {
        val result = runJob(listOf(Step("checkout") { 0 }, Step("build") { 0 }))
        assertEquals(JobResult(true, listOf("ok checkout", "ok build")), result)
    }

    @Test fun `a non-zero exit fails the job and skips the rest`() {
        val result = runJob(listOf(Step("build") { 1 }, Step("publish") { 0 }))
        assertEquals(JobResult(false, listOf("FAILED build (exit 1)", "skipped publish")), result)
    }

    @Test fun `skipped steps never run`() {
        var published = false
        runJob(listOf(Step("build") { 2 }, Step("publish") { published = true; 0 }))
        assertEquals("publish ran after a failed build", false, published)
    }

    @Test fun `an always step runs after a failure, and the job stays failed`() {
        val result = runJob(listOf(Step("test") { 1 }, Step("lint") { 0 }, Step("upload report", always = true) { 0 }))
        assertEquals(JobResult(false, listOf("FAILED test (exit 1)", "skipped lint", "ok upload report")), result)
    }

    @Test fun `a step that throws is a failure, not a crash`() {
        val result = runJob(listOf(Step("build") { error("disk full") }, Step("test") { 0 }))
        assertEquals(JobResult(false, listOf("FAILED build (threw IllegalStateException)", "skipped test")), result)
    }
}
