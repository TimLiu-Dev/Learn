import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CfErrors {
    static CompletableFuture<Integer> parsePort(String s) {
        return CompletableFuture.supplyAsync(() -> Integer.parseInt(s));
    }
    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    public static void main(String[] args) throws InterruptedException {
        // exceptionally: recover with a fallback value; the thenApply step is skipped
        CompletableFuture<Integer> port = parsePort("80x0")
            .thenApply(p -> { System.out.println("never printed"); return p * 2; })
            .exceptionally(e -> { System.out.println("exceptionally saw: " + e); return 8080; });
        System.out.println("port = " + port.join());

        // handle: always runs, gets (result, exception), one of them is null
        CompletableFuture<String> h = parsePort("443")
            .handle((v, e) -> e == null ? "ok " + v : "failed: " + e.getCause().getMessage());
        System.out.println("handle -> " + h.join());

        // whenComplete: look but don't touch; the failure still propagates
        CompletableFuture<Integer> w = parsePort("oops").whenComplete((v, e) ->
            System.out.println("whenComplete logged: " + (e == null ? null : e.getCause().getClass().getSimpleName())));

        // join() vs get(): same failure, different wrapper
        try { w.join(); } catch (CompletionException e) {
            System.out.println("join threw " + e.getClass().getSimpleName() + ", cause " + e.getCause().getClass().getSimpleName());
        }
        try { w.get(); } catch (ExecutionException e) {
            System.out.println("get threw " + e.getClass().getSimpleName() + ", cause " + e.getCause().getClass().getSimpleName());
        }

        // timeouts (JDK 9+)
        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> { sleep(500); return "slow answer"; });
        System.out.println("completeOnTimeout -> " + slow.completeOnTimeout("cached answer", 100, TimeUnit.MILLISECONDS).join());
        CompletableFuture<String> slow2 = CompletableFuture.supplyAsync(() -> { sleep(500); return "slow answer"; });
        try { slow2.orTimeout(100, TimeUnit.MILLISECONDS).join(); }
        catch (CompletionException e) { System.out.println("orTimeout -> " + e.getCause()); }

        // a failure nobody looks at: no output, no stack trace, nothing
        CompletableFuture.runAsync(() -> { throw new IllegalStateException("silent"); });
        Thread.sleep(100);
        System.out.println("done");
    }
}
