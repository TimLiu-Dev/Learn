import java.util.*;

public class Practice {
    static List<String> executionOrder(Map<String, List<String>> tasks, List<String> requested) {
        throw new UnsupportedOperationException("TODO: visit each requested task depth first; add a task after its dependencies, once");
    }

    // CHECKS
    public static void main(String[] args) {
        Map<String, List<String>> build = new LinkedHashMap<>();
        build.put("compileKotlin", List.of());
        build.put("processResources", List.of());
        build.put("classes", List.of("compileKotlin", "processResources"));
        build.put("jar", List.of("classes"));
        build.put("test", List.of("classes"));
        build.put("assemble", List.of("jar"));
        build.put("check", List.of("test"));
        build.put("build", List.of("assemble", "check"));
        build.put("javadoc", List.of("classes"));
        check("dependencies run first", List.of("compileKotlin", "processResources", "classes", "jar"), () -> executionOrder(build, List.of("jar")));
        check("a shared dependency runs once", List.of("compileKotlin", "processResources", "classes", "jar", "assemble", "test", "check", "build"), () -> executionOrder(build, List.of("build")));
        check("tasks nobody needs do not run", false, () -> executionOrder(build, List.of("build")).contains("javadoc"));
        check("an unknown task is named", true, () -> {
            try { executionOrder(Map.of("deploy", List.of("pubish")), List.of("deploy")); return false; }
            catch (IllegalArgumentException e) { return e.getMessage().contains("pubish"); }
        });
        check("a cycle is reported with its path", true, () -> {
            try { executionOrder(Map.of("a", List.of("b"), "b", List.of("a")), List.of("a")); return false; }
            catch (IllegalStateException e) { return e.getMessage().contains("a -> b -> a"); }
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception | StackOverflowError e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
