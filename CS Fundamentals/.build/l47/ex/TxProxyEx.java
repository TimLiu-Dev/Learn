import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TxProxyEx {
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) @interface Transactional {}

    interface LoanDesk {
        void lend(int bookId);
        void lendBoth(int a, int b);
        void importLoan(int bookId) throws IOException;
    }

    static class SimpleLoanDesk implements LoanDesk {
        @Transactional
        public void lend(int bookId) {
            if (bookId == 13) throw new IllegalStateException("book 13 is on loan");
            System.out.println("    insert loan for book " + bookId);
        }

        @Transactional                                   // now the whole method is one unit of work
        public void lendBoth(int a, int b) { lend(a); lend(b); }

        @Transactional
        public void importLoan(int bookId) throws IOException {
            System.out.println("    insert loan for book " + bookId);
            throw new IOException("import file truncated");      // a checked exception
        }
    }

    // What @Transactional does underneath: a proxy that wraps annotated calls in BEGIN/COMMIT/ROLLBACK.
    static <T> T transactional(T target, Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            Method impl = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (!impl.isAnnotationPresent(Transactional.class)) return method.invoke(target, args);
            System.out.println("  BEGIN");
            try {
                Object result = method.invoke(target, args);
                System.out.println("  COMMIT");
                return result;
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                // Spring's default rule: roll back on RuntimeException and Error; commit on checked exceptions.
                System.out.println(t instanceof RuntimeException || t instanceof Error
                        ? "  ROLLBACK" : "  COMMIT (checked " + t.getClass().getSimpleName() + ")");
                throw t;
            }
        }));
    }

    interface Call { void run() throws Exception; }

    public static void main(String[] args) {
        LoanDesk desk = transactional(new SimpleLoanDesk(), LoanDesk.class);
        String[] names = {"lendBoth(2, 3)", "lendBoth(2, 13)"};
        Call[] calls = {() -> desk.lendBoth(2, 3), () -> desk.lendBoth(2, 13)};
        for (int i = 0; i < calls.length; i++) {
            System.out.println("desk." + names[i]);
            try { calls[i].run(); }
            catch (Throwable e) { System.out.println("  -> caller sees " + e.getClass().getSimpleName() + ": " + e.getMessage()); }
        }
    }
}
