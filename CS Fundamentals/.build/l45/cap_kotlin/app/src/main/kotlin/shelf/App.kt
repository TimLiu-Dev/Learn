package shelf

import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.file.Path
import java.time.Clock
import java.util.concurrent.Executors
import shelf.http.Router
import shelf.http.shelfRoutes
import shelf.service.BookService
import shelf.service.LendingService
import shelf.store.Db
import shelf.store.SqliteBookStore
import shelf.store.SqliteLoanStore
import shelf.store.SqliteMemberStore

// The composition root (L38): the one place that names concrete classes and wires them together.
class Shelf(dbFile: Path, port: Int, clock: Clock) : AutoCloseable {
    private val executor = Executors.newVirtualThreadPerTaskExecutor()     // one virtual thread per request (L28)
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0)

    init {
        val db = Db(dbFile).also { it.createSchema() }
        val bookStore = SqliteBookStore(db)
        val loanStore = SqliteLoanStore(db)
        val router = Router()
        router.shelfRoutes(
            BookService(bookStore, loanStore),
            LendingService(bookStore, SqliteMemberStore(db), loanStore, clock),
        )
        server.createContext("/", router::handle)
        server.executor = executor
    }

    val port: Int get() = server.address.port

    fun start(): Shelf = apply { server.start() }

    override fun close() {
        server.stop(0)
        executor.close()
    }
}

fun main(args: Array<String>) {
    val port = args.getOrNull(0)?.toInt() ?: 8080
    val db = Path.of(args.getOrNull(1) ?: "shelf.db")
    val shelf = Shelf(db, port, Clock.systemUTC()).start()
    println("Shelf listening on http://127.0.0.1:${shelf.port} (database: $db)")
}
