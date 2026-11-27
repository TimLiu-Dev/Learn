public class Scoped {
    static final ThreadLocal<String> OLD_USER = new ThreadLocal<>();     // the old way
    static final ScopedValue<String> USER = ScopedValue.newInstance();   // JDK 25+

    static void audit(String how, String user) { System.out.println(how + ": acting for " + user); }

    public static void main(String[] args) {
        OLD_USER.set("ada");                         // set, and remember to remove
        try { audit("ThreadLocal", OLD_USER.get()); } finally { OLD_USER.remove(); }

        ScopedValue.where(USER, "bob").run(() -> audit("ScopedValue", USER.get()));
        System.out.println("after run: bound = " + USER.isBound());     // the binding ended with the block
    }
}
