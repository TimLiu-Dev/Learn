package demo;

import com.google.common.base.Strings;

public class App {
    static String greet(String name) { return "Hello, " + name + "!"; }

    public static void main(String[] args) {
        System.out.println(greet(args.length > 0 ? args[0] : "World"));
        System.out.println(Strings.repeat("=", 13));   // Guava: a real dependency
    }
}
