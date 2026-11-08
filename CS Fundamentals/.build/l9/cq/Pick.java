public class Pick {
    // What fun Any?.show() and fun String?.show() compile to:
    static String show(Object o) { return "any"; }
    static String show(String s) { return "string"; }

    public static void main(String[] args) {
        Object o = "hi";
        String s = null;
        System.out.println(show(o) + " " + show(s) + " " + show(null));
    }
}
