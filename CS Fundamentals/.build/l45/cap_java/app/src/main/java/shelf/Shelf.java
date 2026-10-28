package shelf;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import shelf.http.Router;
import shelf.http.Routes;
import shelf.service.BookService;
import shelf.service.LendingService;
import shelf.store.Db;
import shelf.store.SqliteBookStore;
import shelf.store.SqliteLoanStore;
import shelf.store.SqliteMemberStore;

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

    public int port() {
        return server.getAddress().getPort();
    }

    public Shelf start() {
        server.start();
        return this;
    }

    @Override
    public void close() {
        server.stop(0);
        executor.close();
    }
}
