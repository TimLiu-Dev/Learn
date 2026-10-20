import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

fun parsePort(s: String): CompletableFuture<Int> = CompletableFuture.supplyAsync { s.toInt() }

fun main() {
    // exceptionally: recover with a fallback value; the thenApply step is skipped
    val port = parsePort("80x0")
        .thenApply { println("never printed"); it * 2 }
        .exceptionally { e -> println("exceptionally saw: $e"); 8080 }
    println("port = ${port.join()}")

    // handle: always runs, gets (result, exception), one of them is null
    val h = parsePort("443").handle { v, e -> if (e == null) "ok $v" else "failed: ${e.cause?.message}" }
    println("handle -> ${h.join()}")

    // whenComplete: look but don't touch; the failure still propagates
    val w = parsePort("oops").whenComplete { _, e -> println("whenComplete logged: ${e?.cause?.javaClass?.simpleName}") }

    // join() vs get(): same failure, different wrapper
    try { w.join() } catch (e: CompletionException) { println("join threw ${e.javaClass.simpleName}, cause ${e.cause?.javaClass?.simpleName}") }
    try { w.get() } catch (e: ExecutionException) { println("get threw ${e.javaClass.simpleName}, cause ${e.cause?.javaClass?.simpleName}") }

    // timeouts (JDK 9+)
    val slow = CompletableFuture.supplyAsync { Thread.sleep(500); "slow answer" }
    println("completeOnTimeout -> ${slow.completeOnTimeout("cached answer", 100, TimeUnit.MILLISECONDS).join()}")
    val slow2 = CompletableFuture.supplyAsync { Thread.sleep(500); "slow answer" }
    try { slow2.orTimeout(100, TimeUnit.MILLISECONDS).join() }
    catch (e: CompletionException) { println("orTimeout -> ${e.cause}") }

    // a failure nobody looks at: no output, no stack trace, nothing
    CompletableFuture.runAsync { throw IllegalStateException("silent") }
    Thread.sleep(100)
    println("done")
}
