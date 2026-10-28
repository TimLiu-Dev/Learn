package shelf;

import static shelf.http.AccessLogFilter.jsonLine;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;

public final class App {
    public static void main(String[] args) throws Exception {
        Config config = Config.fromEnv(System.getenv());
        Shelf shelf = new Shelf(config, Clock.systemUTC(), System.out::println, b -> b).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {               // Ctrl-C or kill (SIGTERM)
            Map<String, Object> stopping = new LinkedHashMap<>();             // LinkedHashMap: keys stay in order
            stopping.put("level", "INFO");
            stopping.put("msg", "shutting down");
            stopping.put("drainSeconds", config.drainSeconds());
            System.out.println(jsonLine(stopping));
            shelf.close();
            Map<String, Object> stopped = new LinkedHashMap<>();
            stopped.put("level", "INFO");
            stopped.put("msg", "stopped");
            System.out.println(jsonLine(stopped));
        }));
        Map<String, Object> started = new LinkedHashMap<>();
        started.put("level", "INFO");
        started.put("msg", "listening");
        started.put("port", shelf.port());
        started.put("db", config.dbFile());
        started.put("pool", config.poolSize());
        started.put("cache", config.cacheSize());
        started.put("rateBurst", config.rateCapacity());
        System.out.println(jsonLine(started));
    }
}
