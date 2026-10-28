import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

public class MiniSpring {
    // Java's default retention is CLASS (invisible at runtime, L25), so say RUNTIME explicitly.
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) @interface Get { String value(); }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) @interface Post { String value(); }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.PARAMETER) @interface PathVar { String value(); }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) @interface OnError { Class<? extends Throwable> value(); }

    record Reply(int status, String body) {}

    // What you write: a controller with annotated methods. No routing code at all.
    public static class BookController {
        private final Map<Long, String> books = Map.of(7L, "Dune", 8L, "Emma");

        @Get("/books/{id}")
        public String book(@PathVar("id") long id) {
            String title = books.get(id);
            if (title == null) throw new NoSuchElementException("No book " + id);
            return "{\"id\":" + id + ",\"title\":\"" + title + "\"}";
        }

        @Get("/boom")
        public String boom() { throw new IllegalAccessError("simulated JVM error"); }   // an Error, as in L45

        @OnError(NoSuchElementException.class)
        public Reply notFound(NoSuchElementException e) {
            return new Reply(404, "{\"title\":\"Not Found\",\"detail\":\"" + e.getMessage() + "\"}");
        }

        @OnError(IllegalAccessError.class)
        public Reply jvmError(IllegalAccessError e) { return new Reply(500, "{\"title\":\"Internal Server Error\"}"); }
    }

    static class HandlerDispatchException extends Exception {
        HandlerDispatchException(Throwable cause) { super("Handler dispatch failed: " + cause, cause); }
    }

    // What the framework writes: scan, match, convert, invoke, map errors.
    static class MiniDispatcher {
        private record Route(String method, List<String> template, Method target) {}

        private final Object controller;
        private final List<Route> routes = new ArrayList<>();
        private final Map<Class<?>, Method> errorHandlers = new LinkedHashMap<>();

        MiniDispatcher(Object controller) {
            this.controller = controller;
            for (Method m : controller.getClass().getDeclaredMethods()) {
                Get get = m.getAnnotation(Get.class);
                if (get != null) routes.add(new Route("GET", split(get.value()), m));
                Post post = m.getAnnotation(Post.class);
                if (post != null) routes.add(new Route("POST", split(post.value()), m));
                OnError onError = m.getAnnotation(OnError.class);
                if (onError != null) errorHandlers.put(onError.value(), m);
            }
        }

        String dispatch(String method, String path) throws ReflectiveOperationException {
            List<String> parts = split(path);
            List<Route> shapeFits = routes.stream().filter(r -> bind(r.template(), parts) != null).toList();
            if (shapeFits.isEmpty()) return "404 no handler";
            Optional<Route> found = shapeFits.stream().filter(r -> r.method().equals(method)).findFirst();
            if (found.isEmpty()) return "405 allowed=" + shapeFits.stream().map(Route::method).distinct().toList();
            Route route = found.get();
            Map<String, String> vars = bind(route.template(), parts);
            Object[] args;
            try {
                Parameter[] ps = route.target().getParameters();
                args = new Object[ps.length];
                for (int i = 0; i < ps.length; i++) {
                    String raw = vars.get(ps[i].getAnnotation(PathVar.class).value());
                    args[i] = ps[i].getType() == long.class ? Long.parseLong(raw) : raw;   // type conversion
                }
            } catch (NumberFormatException e) {
                return "400 cannot convert path variable: " + e.getMessage();
            }
            Exception failure;
            try {
                return "200 " + route.target().invoke(controller, args);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getTargetException();
                failure = cause instanceof Exception ex ? ex
                        : new HandlerDispatchException(cause);         // like DispatcherServlet: wrap an Error
            }
            // Walk the cause chain, as @ExceptionHandler matching does.
            for (Throwable t = failure; t != null; t = t.getCause()) {
                for (var entry : errorHandlers.entrySet()) {
                    if (entry.getKey().isInstance(t)) {
                        Reply r = (Reply) entry.getValue().invoke(controller, t);
                        return r.status() + " " + r.body() + "   (handled " + t.getClass().getSimpleName()
                                + " via " + entry.getValue().getName() + ")";
                    }
                }
            }
            return "500 unhandled " + failure.getClass().getSimpleName();
        }

        private static Map<String, String> bind(List<String> template, List<String> parts) {
            if (template.size() != parts.size()) return null;
            Map<String, String> vars = new HashMap<>();
            for (int i = 0; i < template.size(); i++) {
                String t = template.get(i), p = parts.get(i);
                if (t.startsWith("{")) vars.put(t.substring(1, t.length() - 1), p);
                else if (!t.equals(p)) return null;
            }
            return vars;
        }

        private static List<String> split(String path) {
            return Stream.of(path.split("/")).filter(s -> !s.isEmpty()).toList();
        }
    }

    public static void main(String[] args) throws Exception {
        MiniDispatcher dispatcher = new MiniDispatcher(new BookController());
        String[][] calls = {{"GET", "/books/7"}, {"GET", "/books/99"}, {"GET", "/books/abc"},
                            {"POST", "/books/7"}, {"GET", "/boom"}, {"GET", "/authors"}};
        for (String[] c : calls) System.out.println(c[0] + " " + c[1] + " -> " + dispatcher.dispatch(c[0], c[1]));
    }
}
