import java.time.Instant;
import java.util.List;
import tools.jackson.databind.json.JsonMapper;

public class Ex1 {
    static final String PAYLOAD = """
        {"orderId": "A-1001", "placedAt": "2026-10-20T14:05:00Z",
         "customer": {"name": "Ada", "email": "ada@example.com", "loyaltyTier": "gold"},
         "lines": [{"sku": "BK-1", "qty": 2, "priceCents": 1299},
                   {"sku": "BK-7", "qty": 1, "priceCents": 2450}],
         "coupon": null, "giftWrap": true}
        """;

    record Customer(String name) {}                    // only what we need
    record Line(String sku, int qty, long priceCents) {}
    record Order(String orderId, Instant placedAt, Customer customer, List<Line> lines, String coupon) {}
    record Summary(String orderId, int items, long totalCents) {}

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();     // Jackson 3 ignores unknown keys by default
        Order order = mapper.readValue(PAYLOAD, Order.class);
        int items = order.lines().stream().mapToInt(Line::qty).sum();
        long total = order.lines().stream().mapToLong(l -> l.qty() * l.priceCents()).sum();
        System.out.println(order.customer().name() + " ordered " + items + " items on "
            + order.placedAt().toString().substring(0, 10));
        System.out.printf("total: $%d.%02d%n", total / 100, total % 100);
        System.out.println(mapper.writeValueAsString(new Summary(order.orderId(), items, total)));
    }
}
