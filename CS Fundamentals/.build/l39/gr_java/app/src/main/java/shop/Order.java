package shop;

import java.util.ArrayList;
import java.util.List;

// BUILDER (Effective Java Item 2): six fields, three optional
public final class Order {
    private final String id, customer, coupon, notifyBy;
    private final List<Line> lines;
    private final boolean member;

    private Order(Builder b) {
        id = b.id; customer = b.customer; lines = List.copyOf(b.lines);
        member = b.member; coupon = b.coupon; notifyBy = b.notifyBy;
    }

    public static Builder builder(String id, String customer) { return new Builder(id, customer); }

    public String id() { return id; }
    public String customer() { return customer; }
    public List<Line> lines() { return lines; }
    public boolean member() { return member; }
    public String coupon() { return coupon; }
    public String notifyBy() { return notifyBy; }
    public int itemCount() { return lines.stream().mapToInt(Line::qty).sum(); }
    public long subtotalCents() { return lines.stream().mapToLong(l -> l.qty() * l.unitCents()).sum(); }

    public static final class Builder {
        private final String id, customer;                       // required: constructor arguments
        private final List<Line> lines = new ArrayList<>();
        private boolean member = false;                          // optional: defaults
        private String coupon = null;
        private String notifyBy = "email:orders@shop.test";

        private Builder(String id, String customer) { this.id = id; this.customer = customer; }
        public Builder line(String sku, int qty, long unitCents) { lines.add(new Line(sku, qty, unitCents)); return this; }
        public Builder member(boolean v) { member = v; return this; }
        public Builder coupon(String v) { coupon = v; return this; }
        public Builder notifyBy(String v) { notifyBy = v; return this; }

        public Order build() {
            if (lines.isEmpty()) throw new IllegalArgumentException("order " + id + " has no lines");
            if (lines.stream().anyMatch(l -> l.qty() <= 0)) throw new IllegalArgumentException("order " + id + " has a non-positive quantity");
            return new Order(this);
        }
    }
}
