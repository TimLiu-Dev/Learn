package runner

import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val code = try {
        run(args)
    } catch (e: InputError) {
        System.err.println("error: ${e.message}")
        2
    }
    exitProcess(code)   // after a SIGTERM this blocks; the hook returns and the JVM exits with 143
}

private fun run(args: Array<String>): Int {
    val opts = Options.parse(args)
    val text = try {
        File(opts.file).readText()
    } catch (e: IOException) {
        throw InputError("cannot read ${opts.file}: ${e.message}")
    }
    val plan = Plan.order(JobsFile.parse(text))

    val events = Events(System.out)
    val runner = Runner(plan, opts, events)
    val summaryPrinted = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {
        // Runs on SIGTERM and on every normal exit; requestStop() is false when there's nothing to drain.
        // The JVM halts as soon as this hook returns, so wait here until the summary is out.
        if (runner.requestStop()) summaryPrinted.await(opts.graceMs + 10_000, TimeUnit.MILLISECONDS)
    })

    val counts = runBlocking { runner.run() }
    events.summary(
        "summary total=${plan.size} " + Outcome.entries.joinToString(" ") { "${it.event}=${counts.getValue(it)}" }
    )
    summaryPrinted.countDown()
    return if (counts.getValue(Outcome.DONE) == plan.size) 0 else 1
}
