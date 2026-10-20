import java.util.List;

public class Inline {
    static List<String> days = List.of("Mon", "Tue", "STOP", "Thu");

    static void lambdaReturn() {
        days.forEach(d -> {
            if (d.equals("STOP")) return;   // only leaves the lambda, like continue
            System.out.println("  " + d);
        });
        System.out.println("  end of lambdaReturn()");
    }

    static void loopReturn() {
        for (String d : days) {             // to stop the method, use a loop
            if (d.equals("STOP")) return;
            System.out.println("  " + d);
        }
        System.out.println("  never printed");
    }

    public static void main(String[] args) {
        System.out.println("lambdaReturn:"); lambdaReturn();
        System.out.println("loopReturn:"); loopReturn();
    }
}
