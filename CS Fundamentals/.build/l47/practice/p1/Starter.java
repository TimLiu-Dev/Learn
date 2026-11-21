import java.lang.reflect.Constructor;
import java.util.*;

public class Practice {
    enum Scope { SINGLETON, PROTOTYPE }

    static class Container {
        private final Map<Class<?>, Scope> scopes = new HashMap<>();
        private final Map<Class<?>, Object> singletons = new HashMap<>();
        private final List<Class<?>> creating = new ArrayList<>();   // beans under construction, outermost first

        void register(Class<?> type, Scope scope) {
            scopes.put(type, scope);
        }

        /** Builds (or reuses) the bean for type, injecting its constructor parameters by type. */
        <T> T get(Class<T> type) throws Exception {
            Class<?> impl = implementationOf(type);
            throw new UnsupportedOperationException("TODO: reuse a singleton, detect a cycle, then build " + impl.getSimpleName() + " through its constructor");
        }

        private Class<?> implementationOf(Class<?> want) {
            List<Class<?>> found = scopes.keySet().stream().filter(want::isAssignableFrom).toList();
            if (found.size() != 1) throw new IllegalStateException("No bean of type " + want.getSimpleName());
            return found.get(0);
        }
    }

    // CHECKS
    interface Repo {}
    static class MemoryRepo implements Repo {}
    static class Service { final Repo repo; Service(Repo repo) { this.repo = repo; } }
    static class Handler { final Service service; Handler(Service service) { this.service = service; } }
    static class A { A(B b) {} }
    static class B { B(A a) {} }
    static class Friend {}
    static class Lonely { final Friend friend; Lonely(Friend friend) { this.friend = friend; } }

    static Container shelf() {
        Container c = new Container();
        c.register(MemoryRepo.class, Scope.SINGLETON);
        c.register(Service.class, Scope.SINGLETON);
        c.register(Handler.class, Scope.PROTOTYPE);
        return c;
    }

    static String failure(Check call) {
        try {
            call.get();
            return "no exception";
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        check("a singleton is built once", true, () -> { Container c = shelf(); return c.get(Repo.class) == c.get(Repo.class); });
        check("a prototype is built on every get", false, () -> { Container c = shelf(); return c.get(Handler.class) == c.get(Handler.class); });
        check("prototypes share their singleton dependency", true, () -> { Container c = shelf(); return c.get(Handler.class).service == c.get(Handler.class).service; });
        check("constructor parameters are injected", true, () -> shelf().get(Service.class).repo instanceof MemoryRepo);
        check("a cycle fails with its path", "IllegalStateException: Dependency cycle: A -> B -> A", () -> {
            Container c = new Container();
            c.register(A.class, Scope.SINGLETON);
            c.register(B.class, Scope.SINGLETON);
            return failure(() -> c.get(A.class));
        });
        check("a missing bean names the type", "IllegalStateException: No bean of type Friend", () -> {
            Container c = new Container();
            c.register(Lonely.class, Scope.SINGLETON);
            return failure(() -> c.get(Lonely.class));
        });
        check("a failed get leaves the container usable", true, () -> {
            Container c = new Container();
            c.register(Lonely.class, Scope.SINGLETON);
            failure(() -> c.get(Lonely.class));
            c.register(Friend.class, Scope.SINGLETON);
            return c.get(Lonely.class).friend != null;
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
