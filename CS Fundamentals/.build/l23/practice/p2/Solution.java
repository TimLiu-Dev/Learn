import java.util.*;

public class Practice {
    record ClassDef(String name, String superName, List<String> initUses) {
        ClassDef(String name) { this(name, null, List.of()); }
        ClassDef(String name, String superName) { this(name, superName, List.of()); }
    }

    static class MiniJvm {
        private final Map<String, ClassDef> classes = new HashMap<>();
        private final Set<String> started = new HashSet<>();   // initialisation begun (or finished)
        final List<String> events = new ArrayList<>();

        MiniJvm(List<ClassDef> defs) { for (ClassDef d : defs) classes.put(d.name(), d); }

        // An active use: new, getstatic, putstatic or invokestatic (JVMS §5.5).
        void use(String name) {
            if (!started.add(name)) return;            // already started: done, or in progress (a cycle)
            ClassDef def = classes.get(name);
            if (def.superName() != null) use(def.superName());   // superclass first
            events.add("init " + def.name());          // now <clinit> runs…
            for (String other : def.initUses()) use(other);      // …and touches other classes as it goes
        }
    }

    // CHECKS
    static final List<String> realLog = new ArrayList<>();
    static class Base { static { realLog.add("init Base"); } }
    static class Child extends Base { static { realLog.add("init Child"); Helper.ping(); } }
    static class Helper { static { realLog.add("init Helper"); } static void ping() {} }

    public static void main(String[] args) {
        check("a class initialises on first use, and only once", List.of("init Registry", "init Settings"), () -> {
            MiniJvm jvm = new MiniJvm(List.of(new ClassDef("Registry"), new ClassDef("Settings")));
            jvm.use("Registry"); jvm.use("Settings"); jvm.use("Registry");
            return jvm.events;
        });
        check("nothing initialises until it is used", List.of("init Registry"), () -> {
            MiniJvm jvm = new MiniJvm(List.of(new ClassDef("Registry"), new ClassDef("Unused")));
            jvm.use("Registry");
            return jvm.events;
        });
        check("the superclass initialises first, the whole chain", List.of("init Object", "init Animal", "init Dog"), () -> {
            MiniJvm jvm = new MiniJvm(List.of(new ClassDef("Object"), new ClassDef("Animal", "Object"), new ClassDef("Dog", "Animal")));
            jvm.use("Dog");
            return jvm.events;
        });
        check("an initialiser can trigger other classes", List.of("init Registry", "init CollectionsKt"), () -> {
            MiniJvm jvm = new MiniJvm(List.of(new ClassDef("Registry", null, List.of("CollectionsKt")), new ClassDef("CollectionsKt")));
            jvm.use("Registry");
            return jvm.events;
        });
        check("a cycle between two initialisers ends", List.of("init A", "init B"), () -> {
            MiniJvm jvm = new MiniJvm(List.of(new ClassDef("A", null, List.of("B")), new ClassDef("B", null, List.of("A"))));
            try { jvm.use("A"); } catch (StackOverflowError e) { return "StackOverflowError"; }
            return jvm.events;
        });
        check("the simulation matches the real JVM", List.of("init Base", "init Child", "init Helper"), () -> {
            new Child(); new Child();
            MiniJvm jvm = new MiniJvm(List.of(new ClassDef("Base"), new ClassDef("Child", "Base", List.of("Helper")), new ClassDef("Helper")));
            jvm.use("Child"); jvm.use("Child");
            return realLog.equals(jvm.events) ? jvm.events : "real " + realLog + " vs simulated " + jvm.events;
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
