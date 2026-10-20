import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Calc {
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) { return a / b; }
}

public class Costs {
    public static void main(String[] args) throws Exception {
        Calc calc = new Calc();
        Class<Calc> k = Calc.class;

        // 1. A typo compiles fine and fails only when this line runs.
        try {
            k.getMethod("ad", int.class, int.class);
        } catch (NoSuchMethodException e) {
            System.out.println("1. NoSuchMethodException: " + e.getMessage());
        }

        // 2. Exceptions thrown by the target arrive wrapped.
        try {
            k.getMethod("divide", int.class, int.class).invoke(calc, 1, 0);
        } catch (InvocationTargetException e) {
            System.out.println("2. " + e.getClass().getSimpleName() + ", cause: " + e.getCause());
        }

        // 3. JDK internals are strongly encapsulated (JEP 403).
        try {
            String.class.getDeclaredField("value").setAccessible(true);
        } catch (RuntimeException e) {
            System.out.println("3. " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // 4. A rough speed comparison. Not a benchmark: L24's JMH is the honest tool.
        Method add = k.getMethod("add", int.class, int.class);
        int n = 20_000_000;
        for (int round = 0; round < 3; round++) {
            long sum = 0;
            long t = System.nanoTime();
            for (int i = 0; i < n; i++) sum += calc.add(i, 1);
            double direct = (double) (System.nanoTime() - t) / n;
            t = System.nanoTime();
            for (int i = 0; i < n; i++) sum += (Integer) add.invoke(calc, i, 1);
            double reflective = (double) (System.nanoTime() - t) / n;
            System.out.printf("4. round %d: direct %.2f ns, reflective %.2f ns per call (sum=%d)%n",
                round, direct, reflective, sum);
        }
    }
}
