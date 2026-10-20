import java.util.ArrayList;
import java.util.List;

public class Reified {
    // Java has no reified generics: the caller passes the Class object
    static <T> List<T> only(List<?> items, Class<T> type) {
        List<T> out = new ArrayList<>();
        for (Object x : items) if (type.isInstance(x)) out.add(type.cast(x));
        return out;
    }

    static <T> String typeName(Class<T> type) { return type.getSimpleName(); }

    public static void main(String[] args) {
        List<Object> mixed = List.of("a", 1, "b", 2.5, 3);
        System.out.println(only(mixed, String.class));
        System.out.println(only(mixed, Integer.class));
        System.out.println(only(mixed, Double.class));
        System.out.println(typeName(StringBuilder.class));
    }
}
