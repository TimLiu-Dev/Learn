import java.util.concurrent.*;

public class Bridge {
    // A callback-style client, shaped like OkHttp's enqueue or an Android SDK call.
    interface Callback { void onResponse(String body); void onFailure(Exception e); }
    interface Call { void cancel(); }

    static final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "http"); t.setDaemon(true); return t;
    });

    static Call enqueue(String url, long ms, Callback cb) {
        ScheduledFuture<?> task = timer.schedule(() -> {
            if (url.endsWith("/fail")) cb.onFailure(new IllegalStateException("503 from " + url));
            else cb.onResponse("body of " + url);
        }, ms, TimeUnit.MILLISECONDS);
        return () -> { task.cancel(false); System.out.println("  OldHttp: call to " + url + " cancelled"); };
    }

    // Nearest equivalent: a CompletableFuture the callback completes.
    static CompletableFuture<String> get(String url, long ms) {
        CompletableFuture<String> f = new CompletableFuture<>();
        Call call = enqueue(url, ms, new Callback() {
            public void onResponse(String body) { f.complete(body); }
            public void onFailure(Exception e) { f.completeExceptionally(e); }
        });
        f.whenComplete((v, e) -> {                     // timed out or cancelled: stop the real work
            if (e instanceof CancellationException || e instanceof TimeoutException) call.cancel();
        });
        return f;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(get("/books", 50).get());
        try { get("/fail", 50).join(); }
        catch (CompletionException e) { System.out.println("caught: " + e.getCause().getMessage()); }
        try { get("/slow", 1_000).orTimeout(100, TimeUnit.MILLISECONDS).join(); }
        catch (CompletionException e) { System.out.println("with a 100 ms timeout: " + e.getCause()); }
    }
}
