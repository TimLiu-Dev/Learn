import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy

@Target(AnnotationTarget.FUNCTION) annotation class Transactional

interface LoanDesk {
    fun lend(bookId: Int)
    fun lendBoth(a: Int, b: Int)
    @Throws(IOException::class)
    fun importLoan(bookId: Int)
}

class SimpleLoanDesk : LoanDesk {
    @Transactional
    override fun lend(bookId: Int) {
        if (bookId == 13) throw IllegalStateException("book 13 is on loan")
        println("    insert loan for book $bookId")
    }

    // Not annotated: it calls lend() on `this`, the real object, not on the proxy.
    override fun lendBoth(a: Int, b: Int) { lend(a); lend(b) }

    @Transactional
    override fun importLoan(bookId: Int) {
        println("    insert loan for book $bookId")
        throw IOException("import file truncated")          // a checked exception type
    }
}

// What @Transactional does underneath: a proxy that wraps annotated calls in BEGIN/COMMIT/ROLLBACK.
fun <T : Any> transactional(target: T, type: Class<T>): T =
    type.cast(Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, args ->
        val impl = target.javaClass.getMethod(method.name, *method.parameterTypes)
        if (!impl.isAnnotationPresent(Transactional::class.java)) return@newProxyInstance method.invoke(target, *(args ?: arrayOf()))
        println("  BEGIN")
        try {
            method.invoke(target, *(args ?: arrayOf())).also { println("  COMMIT") }
        } catch (e: InvocationTargetException) {
            val t = e.targetException
            // Spring's default rule: roll back on RuntimeException and Error; commit on checked exceptions.
            println(if (t is RuntimeException || t is Error) "  ROLLBACK" else "  COMMIT (checked ${t.javaClass.simpleName})")
            throw t
        }
    })

fun main() {
    val desk = transactional(SimpleLoanDesk(), LoanDesk::class.java)
    val calls = listOf<Pair<String, () -> Unit>>(
        "lend(1)" to { desk.lend(1) },
        "lend(13)" to { desk.lend(13) },
        "lendBoth(2, 3)" to { desk.lendBoth(2, 3) },
        "importLoan(4)" to { desk.importLoan(4) },
    )
    for ((name, call) in calls) {
        println("desk.$name")
        try { call() } catch (e: Throwable) { println("  -> caller sees ${e.javaClass.simpleName}: ${e.message}") }
    }
}
