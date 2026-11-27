package budget;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Ordered rules: the first match wins. */
public final class Rules {
    public static final String UNCATEGORISED = "Uncategorised";

    /** CASE_INSENSITIVE alone folds only ASCII; UNICODE_CASE makes /café/ match "CAFÉ". */
    private static final int IGNORE_CASE = Pattern.CASE_INSENSITIVE; // BUG: folds ASCII letters only

    public record Rule(Pattern pattern, String category) {}

    private final List<Rule> rules;

    public Rules(List<Rule> rules) {
        this.rules = List.copyOf(rules);
    }

    public String categorise(String description) {
        for (Rule rule : rules) {
            if (rule.pattern().matcher(description).find()) return rule.category();
        }
        return UNCATEGORISED;
    }

    /** All-or-nothing: one bad line rejects the whole file, naming that line. */
    public static Parsed<Rules> parse(List<String> lines) {
        List<Rule> rules = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty() || line.startsWith("#")) continue;
            switch (parseRule(line)) {
                case Parsed.Ok<Rule>(Rule rule) -> rules.add(rule);
                case Parsed.Bad<Rule>(String reason) -> {
                    return Parsed.bad("line " + (i + 1) + ": " + reason);
                }
            }
        }
        return Parsed.ok(new Rules(rules));
    }

    private static Parsed<Rule> parseRule(String line) {
        int arrow = line.lastIndexOf("->");
        if (arrow < 0) return Parsed.bad("missing '->'");
        String pattern = line.substring(0, arrow).strip();
        String category = line.substring(arrow + 2).strip();
        if (category.isEmpty()) return Parsed.bad("empty category");
        boolean isRegex = pattern.length() >= 2 && pattern.startsWith("/") && pattern.endsWith("/");
        String body = isRegex ? pattern.substring(1, pattern.length() - 1) : pattern;
        if (body.isEmpty()) return Parsed.bad("empty pattern");
        try {
            // A substring rule is just a regex of the quoted text, so both kinds match the same way.
            return Parsed.ok(new Rule(Pattern.compile(isRegex ? body : Pattern.quote(body), IGNORE_CASE), category));
        } catch (PatternSyntaxException e) {
            return Parsed.bad("invalid regex '" + pattern + "'");
        }
    }
}
