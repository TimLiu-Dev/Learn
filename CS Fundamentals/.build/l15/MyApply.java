import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyApply {
    // Java has no "lambda with receiver": the object must be a named parameter
    static <T> T apply(T obj, Consumer<T> block) {
        block.accept(obj);
        return obj;
    }

    static <T, R> R let(T obj, Function<T, R> block) { return block.apply(obj); }

    static class HtmlList {
        private final List<String> items = new ArrayList<>();
        void item(String text) { items.add("<li>" + text + "</li>"); }
        String render() { return "<ul>" + String.join("", items) + "</ul>"; }
    }

    static String htmlList(Consumer<HtmlList> build) { return apply(new HtmlList(), build).render(); }

    public static void main(String[] args) {
        StringBuilder sb = apply(new StringBuilder(), b -> { b.append("built "); b.append("with myApply"); });
        System.out.println(sb);
        System.out.println(let("kotlin", String::length));

        Function<String, String> greet = s -> "hello, " + s;
        System.out.println(greet.apply("ada"));
        System.out.println(greet.apply("bob"));

        System.out.println(apply(new StringBuilder(), b -> { b.append("abc"); b.append(123); }).toString());
        System.out.println(htmlList(l -> { l.item("eggs"); l.item("milk"); }));   // every call needs `l.`
    }
}
