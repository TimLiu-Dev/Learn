public class Relay {
    static <T> String what(T x) { return "T"; }
    static String what(String s) { return "String"; }

    static <T> String relay(T x) { return what(x); }

    public static void main(String[] args) {
        System.out.println(what("hi") + " " + relay("hi"));
    }
}
