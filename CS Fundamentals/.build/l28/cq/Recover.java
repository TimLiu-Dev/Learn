import java.util.concurrent.CompletableFuture;

public class Recover {
    public static void main(String[] args) {
        CompletableFuture<String> f = CompletableFuture
                .supplyAsync(() -> Integer.parseInt("4x2"))
                .thenApply(n -> "doubled " + (n * 2))
                .exceptionally(e -> "recovered from " + e.getClass().getSimpleName());
        System.out.println(f.join());
    }
}
