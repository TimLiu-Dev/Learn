// Excerpt of L45 Shelf.java, lines 20–37 (tested there; not a standalone file)
// The composition root (L38): the one place that names concrete classes and wires them together.
public final class Shelf implements AutoCloseable {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();   // L28
    private final HttpServer server;

    public Shelf(Path dbFile, int port, Clock clock) throws IOException {
        Db db = new Db(dbFile);
        db.createSchema();
        var bookStore = new SqliteBookStore(db);
        var loanStore = new SqliteLoanStore(db);
        Router router = new Router();
        Routes.register(router,
            new BookService(bookStore, loanStore),
            new LendingService(bookStore, new SqliteMemberStore(db), loanStore, clock));
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/", router::handle);
        server.setExecutor(executor);
    }
