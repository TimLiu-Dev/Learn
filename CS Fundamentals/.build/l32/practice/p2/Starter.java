import java.util.*;

public class Practice {
    record Response(int status, String body) { }

    static class Orders {
        final Map<Long, String> orders = new LinkedHashMap<>();   // id -> item: the server's state
        private long nextId = 1;

        // POST /orders, with an optional Idempotency-Key header (null if absent)
        Response post(String item, String key) {
            long id = nextId++;                                   // TODO: look the key up first
            orders.put(id, item);
            return new Response(201, "order " + id + ": " + item);
        }

        // PUT /orders/{id}: "make order id look like this". Idempotent already.
        Response put(long id, String item) {
            boolean existed = orders.put(id, item) != null;
            return new Response(existed ? 200 : 201, "order " + id + ": " + item);
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("a POST without a key creates every time", Map.of(1L, "Dune", 2L, "Dune"), () -> {
            Orders shop = new Orders();
            shop.post("Dune", null);
            shop.post("Dune", null);
            return shop.orders;
        });
        check("a retried POST with the same key creates one order", Map.of(1L, "Dune"), () -> {
            Orders shop = new Orders();
            for (int i = 0; i < 3; i++) shop.post("Dune", "k-1");
            return shop.orders;
        });
        check("the retry gets the original response back", List.of(new Response(201, "order 1: Dune"), new Response(201, "order 1: Dune")), () -> {
            Orders shop = new Orders();
            return List.of(shop.post("Dune", "k-1"), shop.post("Dune", "k-1"));
        });
        check("different keys are different orders, even for the same item", Map.of(1L, "Dune", 2L, "Dune"), () -> {
            Orders shop = new Orders();
            shop.post("Dune", "k-1");
            shop.post("Dune", "k-2");
            return shop.orders;
        });
        check("a key reused for a different item is refused with 422", "422 " + Map.of(1L, "Dune"), () -> {
            Orders shop = new Orders();
            shop.post("Dune", "k-1");
            Response reused = shop.post("Emma", "k-1");
            return reused.status() + " " + shop.orders;
        });
        check("a replayed PUT leaves the same state", "201 200 " + Map.of(42L, "Dune"), () -> {
            Orders shop = new Orders();
            int first = shop.put(42, "Dune").status();
            int again = shop.put(42, "Dune").status();
            return first + " " + again + " " + shop.orders;
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
