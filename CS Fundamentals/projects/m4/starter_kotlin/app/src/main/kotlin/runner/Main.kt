package runner

import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val code = try {
        run(args)
    } catch (e: InputError) {
        System.err.println("error: ${e.message}")
        2
    }
    exitProcess(code)
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
    // Milestone 5: a shutdown hook (L53) that drains before the JVM halts.

    val counts = runBlocking { runner.run() }
    events.summary(
        "summary total=${plan.size} " + Outcome.entries.joinToString(" ") { "${it.event}=${counts.getValue(it)}" }
    )
    return if (counts.getValue(Outcome.DONE) == plan.size) 0 else 1
}
