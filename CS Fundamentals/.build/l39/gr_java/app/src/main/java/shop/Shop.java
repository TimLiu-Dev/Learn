package shop;

import java.util.List;
import java.util.function.Function;

// Everything the shop needs is injected (L38), so each pattern can be swapped or faked
public final class Shop {
    private final List<PricingRule> rules;
    private final OrderEvents events;
    private final Function<String, Notifier> notifierFor;

    public Shop(List<PricingRule> rules, OrderEvents events, Function<String, Notifier> notifierFor) {
        this.rules = rules; this.events = events; this.notifierFor = notifierFor;
    }

    public long place(Order order) {
        long total = PricingRule.price(order, rules);
        events.publish(new OrderEvent.Placed(order.id(), total));
        notifierFor.apply(order.notifyBy()).send(order.customer(), "order " + order.id() + " confirmed: " + PricingRule.cents(total));
        events.publish(new OrderEvent.Notified(order.id(), order.notifyBy().split(":")[0]));
        return total;
    }
}
