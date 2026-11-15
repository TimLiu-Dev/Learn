import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Practice {
    // The "framework": it reads annotations through reflection. Don't change it.
    static Map<String, String> columns(Class<?> type) {
        Map<String, String> result = new TreeMap<>();
        for (Field f : type.getDeclaredFields()) {
            Column c = f.getAnnotation(Column.class);
            if (c != null) result.put(f.getName(), c.value());
        }
        return result;
    }

    static List<String> auditedMethods(Class<?> type) {
        List<String> names = new ArrayList<>();
        for (Method m : type.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Audited.class)) names.add(m.getName());
        }
        Collections.sort(names);
        return names;
    }

    // Java's default retention is CLASS, invisible to reflection: say RUNTIME.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Column { String value(); }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Audited {}

    @Retention(RetentionPolicy.SOURCE)
    @interface Secret {}

    static class Account {
        @Column("user_email") String email;
        @Column("display_name") String name;
        @Secret String password;

        @Audited void save() {}
        @Audited void delete() {}
        void refresh() {}
    }

    // CHECKS
    public static void main(String[] args) throws Exception {
        check("columns are read from the fields", Map.of("email", "user_email", "name", "display_name"), () -> columns(Account.class));
        check("audited methods are visible at runtime", List.of("delete", "save"), () -> auditedMethods(Account.class));
        check("Column may be put on fields only", List.of(ElementType.FIELD), () -> {
            Target t = Column.class.getAnnotation(Target.class);
            return t == null ? null : List.of(t.value());
        });
        check("Secret never reaches the class file", RetentionPolicy.SOURCE, () -> {
            Retention r = Secret.class.getAnnotation(Retention.class);
            return r == null ? null : r.value();
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
