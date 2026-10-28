package shelf;

import java.nio.file.Path;
import java.time.Clock;

public final class App {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Path db = Path.of(args.length > 1 ? args[1] : "shelf.db");
        Shelf shelf = new Shelf(db, port, Clock.systemUTC()).start();
        System.out.println("Shelf listening on http://127.0.0.1:" + shelf.port() + " (database: " + db + ")");
    }
}
