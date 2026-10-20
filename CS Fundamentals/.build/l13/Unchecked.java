import java.util.List;

public class Unchecked {
    @SuppressWarnings("unchecked")                       // don't do this without a proof!
    static <T> List<T> castList(List<?> xs) { return (List<T>) xs; }

    static <T> List<T> castListLoud(List<?> xs) { return (List<T>) xs; }

    public static void main(String[] args) {
        List<String> strings = castList(List.of(1, 2));  // no error here...
        System.out.println("size " + strings.size());    // ...or here...
        String first = strings.get(0);                   // ...it blows up HERE
        System.out.println(first);
    }
}
