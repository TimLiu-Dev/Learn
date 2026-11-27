package budget;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class Budgets {
    private Budgets() {}

    /** Monthly budget per category, in file order. All-or-nothing, like the rules. */
    static Parsed<Map<String, Money>> parse(List<String> lines) {
        Map<String, Money> budgets = new LinkedHashMap<>(); // keeps the file's order for the output
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String reason = addLine(line, budgets);
            if (reason != null) return Parsed.bad("line " + (i + 1) + ": " + reason);
        }
        return Parsed.ok(Collections.unmodifiableMap(budgets));
    }

    /** Adds one line to budgets; returns the reason it's bad, or null. */
    private static String addLine(String line, Map<String, Money> budgets) {
        int equals = line.lastIndexOf('=');
        if (equals < 0) return "missing '='";
        String category = line.substring(0, equals).strip();
        String amountText = line.substring(equals + 1).strip();
        if (category.isEmpty()) return "empty category";
        Money amount = Money.parse(amountText);
        if (amount == null) return "invalid amount '" + amountText + "'";
        if (amount.signum() <= 0) return "budget must be positive";
        if (budgets.containsKey(category)) return "duplicate category '" + category + "'";
        budgets.put(category, amount);
        return null;
    }
}
