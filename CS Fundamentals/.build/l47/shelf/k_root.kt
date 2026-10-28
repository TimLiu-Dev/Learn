// Excerpt of L45 App.kt, lines 18–34 (tested there; not a standalone file)
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
