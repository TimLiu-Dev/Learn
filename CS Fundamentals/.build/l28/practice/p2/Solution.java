import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class Practice {
    record User(int id, String name) { }

    static CompletableFuture<String> bill(int userId,
                                          Function<Integer, CompletableFuture<User>> findUser,
                                          Function<User, CompletableFuture<List<Integer>>> ordersFor,
                                          Supplier<CompletableFuture<Integer>> discount) {
        CompletableFuture<Integer> off = discount.get();    // starts now, while the user is looked up
        return findUser.apply(userId)
                .thenCompose(user -> ordersFor.apply(user)   // flatMap: ordersFor returns a future
                        .thenCombine(off, (amounts, d) ->
                                user.name() + " pays " + (amounts.stream().mapToInt(Integer::intValue).sum() - d)))
                .exceptionally(e -> {                        // e arrives wrapped in CompletionException
                    Throwable cause = e instanceof CompletionException && e.getCause() != null ? e.getCause() : e;
                    return "failed: " + cause.getMessage();
                });
    }

    // CHECKS
    static final Function<Integer, CompletableFuture<User>> ADA = id -> CompletableFuture.completedFuture(new User(id, "Ada"));
    static final Function<User, CompletableFuture<List<Integer>>> TWO_ORDERS = u -> CompletableFuture.completedFuture(List.of(500, 600));

    static Supplier<CompletableFuture<Integer>> discountOf(int cents) {
        return () -> CompletableFuture.completedFuture(cents);
    }

    public static void main(String[] args) {
        check("Ada pays her orders minus the discount", "Ada pays 950", () -> bill(1, ADA, TWO_ORDERS, discountOf(150)).join());
        check("orders are fetched for the user that was found", "Bo pays 100", () -> bill(7,
                id -> CompletableFuture.completedFuture(new User(id, "Bo")),
                u -> CompletableFuture.completedFuture(u.id() == 7 ? List.of(100) : List.of(999)),
                discountOf(0)).join());
        check("bill returns the pipeline at once, before the discount arrives", "false, then Ada pays 1050", () -> {
            CompletableFuture<Integer> pending = new CompletableFuture<>();
            try {
                // A guard, not a timing test: correct code returns at once, blocking code would never return.
                CompletableFuture<String> result = CompletableFuture
                        .supplyAsync(() -> bill(1, ADA, TWO_ORDERS, () -> pending)).get(2, TimeUnit.SECONDS);
                boolean doneEarly = result.isDone();
                pending.complete(50);
                return doneEarly + ", then " + result.join();
            } finally {
                pending.complete(0);
            }
        });
        check("a failed stage becomes a message with the real cause", "failed: orders down", () -> bill(1, ADA,
                u -> CompletableFuture.failedFuture(new IllegalStateException("orders down")), discountOf(0)).join());
        check("a failed lookup never asks for orders", "failed: no user 9, 0 orders calls", () -> {
            AtomicInteger calls = new AtomicInteger();
            String text = bill(9,
                    id -> CompletableFuture.failedFuture(new NoSuchElementException("no user " + id)),
                    u -> { calls.incrementAndGet(); return CompletableFuture.completedFuture(List.of(1)); },
                    discountOf(0)).join();
            return text + ", " + calls.get() + " orders calls";
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
