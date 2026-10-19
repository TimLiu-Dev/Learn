import java.util.List;

public class Flow {
    static String grade(int score) {
        if (score < 0 || score > 100) return "invalid";
        else if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }

    static String dayKind(String day) {
        return switch (day) {
            case "SAT", "SUN" -> "weekend";
            case "MON", "TUE", "WED", "THU", "FRI" -> "weekday";
            default -> "unknown";
        };
    }

    public static void main(String[] args) {
        List<Integer> scores = List.of(95, 83, 70, 42, 101);
        for (int s : scores) System.out.println(s + " -> " + grade(s));

        int a = 7;
        int b = 3;
        int bigger = a > b ? a : b;          // the ternary operator
        System.out.println("bigger: " + bigger + ", SAT is a " + dayKind("SAT"));

        for (int i = 10; i >= 0; i -= 3) System.out.print(i + " ");
        System.out.println();
        for (int i = 0; i < 3; i++) System.out.print(i + " ");
        System.out.println();
        for (int index = 0; index < scores.size(); index++) {
            if (index == 2) break;
            System.out.println("#" + index + " = " + scores.get(index));
        }

        int tries = 0;
        do {
            tries++;
        } while (tries < 3);
        System.out.println("tries: " + tries);
    }
}
