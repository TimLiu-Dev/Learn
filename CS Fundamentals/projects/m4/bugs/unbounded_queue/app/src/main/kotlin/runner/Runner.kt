package runner

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job as CoroutineJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withTimeoutOrNull
import java.util.EnumMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Runs a plan (jobs already in topological order) with structured concurrency:
 * a producer feeds a bounded [Channel], a dispatcher takes a slot from a [Semaphore]
 * and then a job, and each job runs in its own coroutine inside one parent [CoroutineJob].
 */
class Runner(private val plan: List<Job>, private val opts: Options, private val events: Events) {
    private val ended = plan.associate { it.id to CompletableDeferred<Outcome>() }
    private val counts = EnumMap<Outcome, AtomicInteger>(Outcome::class.java)
        .apply { Outcome.entries.forEach { put(it, AtomicInteger()) } }
    private val stop = CompletableDeferred<Unit>()

    /** Called from the shutdown hook's thread. False if there is nothing to drain. */
    fun requestStop(): Boolean {
        if (!events.beginShutdown()) return false
        stop.complete(Unit)
        return true
    }

    suspend fun run(): Map<Outcome, Int> = coroutineScope {
        val queue = Channel<Job>(Channel.UNLIMITED)    // BUG: send() never suspends, so no backpressure
        val slots = Semaphore(opts.workers)
        val dispatched = mutableSetOf<String>()         // runBlocking runs all of this on one thread
        val group = CoroutineJob(coroutineContext.job)  // parent of every job coroutine

        val producer = launch {
            for (job in plan) {
                queue.send(job)
                events.emit("queued ${job.id}")
            }
            queue.close()
        }
        val dispatcher = launch {
            while (true) {
                slots.acquire()                         // a slot first, then a job: full slots stop the taking
                val job = queue.receiveCatching().getOrNull() ?: break
                dispatched += job.id
                launch(group) {
                    try { runJob(job) } finally { slots.release() }
                }
            }
        }
        val watcher = launch {
            stop.await()
            producer.cancel()
            dispatcher.cancelAndJoin()
            for (job in plan) if (job.id !in dispatched) finish(job, Outcome.DROP, "drop ${job.id}")
            val drained = withTimeoutOrNull(opts.graceMs) { group.children.toList().joinAll() }
            if (drained == null) group.cancelChildren()   // grace is over: cancel what's still running
        }

        dispatcher.join()          // ends when the channel is closed and empty, or when the watcher cancels it
        group.complete()           // no more children; join() now waits for the ones there are
        group.join()
        if (stop.isCompleted) watcher.join() else watcher.cancel()
        counts.mapValues { it.value.get() }
    }

    private suspend fun runJob(job: Job) {
        val depOutcomes = try {
            job.deps.map { ended.getValue(it).await() }
        } catch (e: CancellationException) {
            finish(job, Outcome.DROP, "drop ${job.id}")
            throw e
        }
        val failedDep = job.deps.zip(depOutcomes).firstOrNull { it.second == Outcome.GIVEUP || it.second == Outcome.SKIP }
        if (failedDep != null) return finish(job, Outcome.SKIP, "skip ${job.id} dep=${failedDep.first}")
        if (depOutcomes.any { it != Outcome.DONE }) return finish(job, Outcome.DROP, "drop ${job.id}")

        var attempt = 1
        while (true) {
            if (!events.startAttempt(job.id, attempt)) return finish(job, Outcome.DROP, "drop ${job.id}")
            val succeeded: Boolean? = try {
                withTimeoutOrNull(if (opts.timeoutMs > 0) opts.timeoutMs else Long.MAX_VALUE) {
                    delay(job.durationMs)               // the simulated work; cancellable
                    attempt > job.fail
                }                                       // null means the timeout cancelled the work
            } catch (e: CancellationException) {
                finish(job, Outcome.CANCEL, "cancel ${job.id} attempt=$attempt")
                throw e
            }
            when (succeeded) {
                true -> return finish(job, Outcome.DONE, "done ${job.id} attempt=$attempt")
                false -> events.emit("fail ${job.id} attempt=$attempt")
                null -> events.emit("timeout ${job.id} attempt=$attempt")
            }
            if (attempt > opts.retries) return finish(job, Outcome.GIVEUP, "giveup ${job.id} attempts=$attempt")
            val wait = backoffMs(attempt)
            events.emit("retry ${job.id} attempt=${attempt + 1} delay=$wait")
            try {
                delay(wait)
            } catch (e: CancellationException) {
                finish(job, Outcome.DROP, "drop ${job.id}")
                throw e
            }
            attempt++
        }
    }

    private fun finish(job: Job, outcome: Outcome, line: String) {
        events.emit(line)
        counts.getValue(outcome).incrementAndGet()
        ended.getValue(job.id).complete(outcome)
    }
}
