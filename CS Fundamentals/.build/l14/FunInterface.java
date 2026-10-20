import java.util.List;

public class FunInterface {
    @FunctionalInterface              // optional, but the compiler then checks it
    interface Rule {
        boolean check(String password);
    }

    static Rule longEnough = pw -> pw.length() >= 8;
    static Rule hasDigit = pw -> pw.chars().anyMatch(Character::isDigit);

    static boolean passesAll(String pw, List<Rule> rules) {
        return rules.stream().allMatch(r -> r.check(pw));
    }

    public static void main(String[] args) {
        List<Rule> rules = List.of(longEnough, hasDigit);
        System.out.println(passesAll("hunter2", rules));
        System.out.println(passesAll("correct7horse", rules));
    }
}
