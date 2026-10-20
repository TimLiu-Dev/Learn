import java.util.concurrent.CompletableFuture;

public class ApplyVsCompose {
    static CompletableFuture<String> lookupAsync(int id) {
        return CompletableFuture.supplyAsync(() -> "user-" + id);
    }

    public static void main(String[] args) {
        CompletableFuture<Integer> id = CompletableFuture.completedFuture(42);

        // thenApply with a function that returns a future: you get a future of a future
        CompletableFuture<CompletableFuture<String>> nested = id.thenApply(ApplyVsCompose::lookupAsync);
        // thenCompose flattens it, like flatMap on a list (L14)
        CompletableFuture<String> flat = id.thenCompose(ApplyVsCompose::lookupAsync);

        System.out.println("nested.join()        = " + nested.join());
        System.out.println("nested.join().join() = " + nested.join().join());
        System.out.println("flat.join()          = " + flat.join());
    }
}
