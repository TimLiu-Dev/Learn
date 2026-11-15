import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Practice {
    @Retention(RetentionPolicy.RUNTIME)   // Java's default (CLASS) would hide it from reflection
    @Target(ElementType.METHOD)
    @interface Check {}

    /** Runs every @Check method of type on a fresh instance, in name order. */
    static List<String> runChecks(Class<?> type) throws Exception {
        List<Method> checks = new ArrayList<>();
        for (Method m : type.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Check.class)) checks.add(m);
        }
        checks.sort(Comparator.comparing(Method::getName));   // no defined order otherwise

        List<String> report = new ArrayList<>();
        for (Method m : checks) {
            Object instance = type.getDeclaredConstructor().newInstance();   // fresh per check
            try {
                m.invoke(instance);
                report.add("PASS " + m.getName());
            } catch (InvocationTargetException e) {
                Throwable real = e.getCause();                // what the method itself threw
                report.add("FAIL " + m.getName() + ": " + real.getClass().getSimpleName() + ": " + real.getMessage());
            }
        }
        return report;
    }

    // CHECKS
    static class Sample {
        int calls = 0;

        @Check void adds() { calls++; }

        @Check void counts() {
            calls++;
            if (calls != 1) throw new IllegalStateException("shared instance: calls=" + calls);
        }

        @Check void divides() {
            int zero = 0;
            System.out.println(10 / zero);
        }

        void helper() { throw new AssertionError("helper is not a check and must not run"); }
    }

    static class NoChecks {
        void plain() {}
    }

    public static void main(String[] args) {
        check("only @Check methods run, in name order", List.of("adds", "counts", "divides"),
                () -> runChecks(Sample.class).stream().map(s -> s.split("[ :]")[1]).toList());
        check("a passing check reports PASS", "PASS adds", () -> runChecks(Sample.class).get(0));
        check("each check gets a fresh instance", "PASS counts", () -> runChecks(Sample.class).get(1));
        check("a failing check reports the real exception", "FAIL divides: ArithmeticException: / by zero", () -> runChecks(Sample.class).get(2));
        check("a class without checks gives an empty report", List.of(), () -> runChecks(NoChecks.class));
    }

    interface Actual { Object get() throws Exception; }

    static void check(String name, Object expected, Actual actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
