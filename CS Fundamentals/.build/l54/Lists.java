import java.util.ArrayList;
import java.util.List;

// Exercise 2: a recursive-descent parser for nested lists of numbers.
//   list := "[" ( item ( "," item )* )? "]"
//   item := NUMBER | list
public class Lists {
    sealed interface Item {}
    record Leaf(int value) implements Item {}
    record Nest(List<Item> items) implements Item {}

    static class ListParser {
        private final String src;
        private int i = 0;

        ListParser(String src) { this.src = src; }

        Nest parse() {
            Nest result = list();
            skipSpaces();
            if (i < src.length()) throw new CalcError("unexpected '" + src.charAt(i) + "' at col " + (i + 1));
            return result;
        }

        private Nest list() {
            expect('[');
            var items = new ArrayList<Item>();
            skipSpaces();
            if (peek() != ']') {
                items.add(item());
                while (peek() == ',') { i++; items.add(item()); }
            }
            expect(']');
            return new Nest(items);
        }

        private Item item() {
            skipSpaces();
            char c = peek();
            if (c == '[') return list();
            if (c >= '0' && c <= '9') {
                int start = i;
                while (peek() >= '0' && peek() <= '9') i++;
                return new Leaf(Integer.parseInt(src.substring(start, i)));
            }
            throw new CalcError("expected a number or '[' at col " + (i + 1));
        }

        private void expect(char c) {
            skipSpaces();
            if (peek() != c) throw new CalcError("expected '" + c + "' at col " + (i + 1));
            i++;
            skipSpaces();
        }

        private void skipSpaces() { while (peek() == ' ') i++; }
        private char peek() { return i < src.length() ? src.charAt(i) : '\0'; }
    }

    static int sum(Item item) {
        return switch (item) {
            case Leaf l -> l.value();
            case Nest n -> n.items().stream().mapToInt(Lists::sum).sum();
        };
    }

    static int depth(Item item) {
        return switch (item) {
            case Leaf l -> 0;
            case Nest n -> 1 + n.items().stream().mapToInt(Lists::depth).max().orElse(0);
        };
    }

    public static void main(String[] args) {
        for (String s : new String[] {"[1, [2, 3], []]", "[[[[7]]]]", "[]", "[1, 2", "[1,, 2]", "[1] 2"}) {
            try {
                Nest tree = new ListParser(s).parse();
                System.out.printf("%-16s sum=%d depth=%d%n", s, sum(tree), depth(tree));
            } catch (CalcError e) {
                System.out.printf("%-16s CalcError: %s%n", s, e.getMessage());
            }
        }
    }
}
