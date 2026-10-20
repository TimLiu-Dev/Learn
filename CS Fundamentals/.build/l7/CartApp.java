import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartApp {
    enum Tier {
        NONE(0), SILVER(5), GOLD(10);
        private final int percentOff;
        Tier(int percentOff) { this.percentOff = percentOff; }
        int percentOff() { return percentOff; }
    }

    record Cart(Map<String, Integer> items, Tier tier) {
        Cart {                                                    // defensive, unmodifiable copy (L5)
            items = Collections.unmodifiableMap(new LinkedHashMap<>(items));
        }
        Cart() { this(Map.of(), Tier.NONE); }
        Cart withItems(Map<String, Integer> newItems) { return new Cart(newItems, tier); }   // hand-written "copy"
        Cart withTier(Tier newTier) { return new Cart(items, newTier); }
    }

    sealed interface Command permits Add, Remove, Upgrade, Clear {}
    record Add(String item, int qty) implements Command {}
    record Remove(String item) implements Command {}
    record Upgrade(Tier tier) implements Command {}
    record Clear() implements Command {}

    static Cart apply(Cart cart, Command command) {
        return switch (command) {
            case Add(String item, int qty) -> {
                Map<String, Integer> items = new LinkedHashMap<>(cart.items());
                items.put(item, items.getOrDefault(item, 0) + qty);
                yield cart.withItems(items);
            }
            case Remove(String item) -> {
                Map<String, Integer> items = new LinkedHashMap<>(cart.items());
                items.remove(item);
                yield cart.withItems(items);
            }
            case Upgrade(Tier tier) -> cart.withTier(tier);
            case Clear c -> cart.withItems(Map.of());
        };
    }

    public static void main(String[] args) {
        List<Command> commands = List.of(new Add("apple", 2), new Add("pear", 1), new Add("apple", 3),
                new Upgrade(Tier.GOLD), new Remove("pear"), new Clear());
        Cart cart = new Cart();
        for (Command command : commands) {
            cart = apply(cart, command);
            System.out.println(command + " -> " + cart);
        }

        Cart oneStep = apply(new Cart(), new Add("apple", 5));
        Cart twoSteps = apply(apply(new Cart(), new Add("apple", 2)), new Add("apple", 3));
        System.out.println(oneStep.equals(twoSteps));

        Cart upgraded = apply(oneStep, new Upgrade(Tier.SILVER));
        if (upgraded instanceof Cart(Map<String, Integer> items, Tier tier)) {   // record pattern
            System.out.println(items + " at " + tier.percentOff() + "% off");
        }
    }
}
