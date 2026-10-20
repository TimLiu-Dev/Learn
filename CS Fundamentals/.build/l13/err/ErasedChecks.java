import java.util.List;

public class ErasedChecks {
    static String describe(Object x) {
        return (x instanceof List<String>) ? "strings" : "other";  // can't check a type argument
    }

    static <T> T make() { return new T(); }                         // can't construct a T

    static String label(List<Integer> xs) { return "ints: " + xs; }
    static String label(List<String> xs) { return "strings: " + xs; } // same erasure: label(List)
}
