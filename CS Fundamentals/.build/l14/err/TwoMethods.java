@FunctionalInterface
interface Rule {
    boolean check(String password);
    String describe();             // a second abstract method
}

public class TwoMethods {
    public static void main(String[] args) {
        Rule r = pw -> pw.length() >= 8;
    }
}
