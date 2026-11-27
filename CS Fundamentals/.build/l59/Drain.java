import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Drain {
    record Reply(int status, String body) { }

    // ---------- the server, run as its own process: java Drain.java server graceful|naive ----------
    static void serve(boolean graceful) throws IOException {
        AtomicBoolean draining = new AtomicBoolean(false);
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        route(server, "/live", () -> new Reply(200, "alive"));                 // the process works: don't restart me
        route(server, "/ready", () -> draining.get() ? new Reply(503, "draining") : new Reply(200, "ready"));  // send me traffic?
        route(server, "/work", () -> { sleep(400); return new Reply(200, "done"); });  // a slow request
        server.start();
        System.out.println("PORT " + server.getAddress().getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (graceful) {
                draining.set(true);
                System.out.println("SIGTERM: /ready now says 503");
                sleep(1000);            // give the load balancer time to notice (it checks every 250 ms)
                System.out.println("stop accepting; waiting for requests in flight");
                server.stop(5);         // returns when the last exchange finishes, or after 5 s
                System.out.println("drained, exiting");
            } else {
                System.out.println("SIGTERM: stopping at once");
                server.stop(0);
            }
        }));
    }

    static void route(HttpServer server, String path, Supplier<Reply> handle) {
        server.createContext(path, ex -> {
            try (ex) {
                Reply r = handle.get();
                ex.sendResponseHeaders(r.status(), r.body().length());
                try (OutputStream out = ex.getResponseBody()) { out.write(r.body().getBytes()); }
            }
        });
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // ---------- the driver: a load balancer that polls /ready, plus steady traffic ----------
    static long t0 = System.nanoTime();
    static long now() { return (System.nanoTime() - t0) / 1_000_000; }
    static void log(String msg) { System.out.println("%5d ms  %s".formatted(now(), msg)); }

    static HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(500)).build();

    static int status(URI base, String path) {
        try {
            return client.send(HttpRequest.newBuilder(base.resolve(path)).build(), HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (IOException | InterruptedException e) {
            return -1;                                                    // -1: no answer at all
        }
    }

    static String show(int s) { return s < 0 ? "no answer" : String.valueOf(s); }

    static void drive(boolean graceful) throws Exception {
        t0 = System.nanoTime();
        String java = ProcessHandle.current().info().command().orElseThrow();
        Process child = new ProcessBuilder(java, "Drain.java", "server", graceful ? "graceful" : "naive")
            .redirectErrorStream(true).start();
        BufferedReader lines = child.inputReader();
        URI base = URI.create("http://127.0.0.1:" + lines.readLine().replace("PORT ", ""));
        Thread.ofPlatform().daemon().start(() -> lines.lines().forEach(l -> log("server: " + l)));

        AtomicBoolean inRotation = new AtomicBoolean(true);
        AtomicBoolean lbStop = new AtomicBoolean(false);
        Thread lb = Thread.ofPlatform().start(() -> {
            while (!lbStop.get()) {
                boolean ready = status(base, "/ready") == 200;
                if (inRotation.getAndSet(ready) != ready) log("balancer: instance " + (ready ? "IN" : "OUT of") + " rotation");
                sleep(250);
            }
        });

        AtomicInteger sent = new AtomicInteger(), ok = new AtomicInteger(), failed = new AtomicInteger();
        List<Thread> requests = new ArrayList<>();
        boolean killed = false;
        while (now() < 3000) {
            if (!killed && now() >= 1000) {
                killed = true;
                log("kill -TERM " + child.pid());
                new ProcessBuilder("kill", "-TERM", String.valueOf(child.pid())).start().waitFor();
                sleep(300);
                log("probes: /live " + show(status(base, "/live")) + ", /ready " + show(status(base, "/ready")));
            }
            if (inRotation.get()) {
                sent.incrementAndGet();
                requests.add(Thread.ofVirtual().start(() -> {
                    if (status(base, "/work") == 200) ok.incrementAndGet(); else failed.incrementAndGet();
                }));
            }
            sleep(50);
        }
        for (Thread t : requests) t.join();
        lbStop.set(true);
        lb.join();
        log("server exit code " + child.waitFor());
        log("requests sent " + sent.get() + ", ok " + ok.get() + ", failed " + failed.get());
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("server")) {
            serve(args[1].equals("graceful"));
            return;
        }
        for (boolean graceful : new boolean[] {false, true}) {
            System.out.println(graceful ? "\n== Graceful: readiness off, drain, then stop" : "== Naive: stop at once on SIGTERM");
            drive(graceful);
        }
    }
}
