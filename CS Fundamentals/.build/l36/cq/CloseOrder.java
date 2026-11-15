import java.util.ArrayList;
import java.util.List;

public class CloseOrder {
    static final List<String> log = new ArrayList<>();

    // Stand-ins for Connection and PreparedStatement: closing just records the name.
    record Res(String name) implements AutoCloseable {
        @Override public void close() { log.add("close " + name); }
    }

    public static void main(String[] args) {
        try (Res conn = new Res("conn"); Res stmt = new Res("stmt")) {
            log.add("query");
            throw new IllegalStateException("boom");
        } catch (IllegalStateException e) {
            log.add("caught " + e.getMessage());
        } finally {
            log.add("finally");
        }
        System.out.println(String.join(", ", log));
    }
}
