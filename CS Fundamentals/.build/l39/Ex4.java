import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Ex4 {
    // 1. An order lifecycle where illegal transitions don't compile.
    sealed interface Order permits Placed, Paid, Shipped, Cancelled {}
    record Placed(List<String> items) implements Order {
        Paid pay(String paymentId) { return new Paid(items, paymentId); }
        Cancelled cancel() { return new Cancelled(false); }
    }
    record Paid(List<String> items, String paymentId) implements Order {
        Shipped ship(String tracking) { return new Shipped(items, tracking); }
        Cancelled cancel() { return new Cancelled(true); }
    }
    record Shipped(List<String> items, String tracking) implements Order {}   // no cancel()
    record Cancelled(boolean refund) implements Order {}

    // 2. Commands that know how to undo themselves.
    static class Editor { final StringBuilder text = new StringBuilder(); }
    interface EditCommand { void run(Editor e); void undo(Editor e); }
    record Insert(String s) implements EditCommand {
        public void run(Editor e) { e.text.append(s); }
        public void undo(Editor e) { e.text.setLength(e.text.length() - s.length()); }
    }
    static final class DeleteLast implements EditCommand {
        private final int n;
        private String removed = "";                                 // remembered for undo
        DeleteLast(int n) { this.n = n; }
        public void run(Editor e) {
            int from = Math.max(0, e.text.length() - n);
            removed = e.text.substring(from);
            e.text.setLength(from);
        }
        public void undo(Editor e) { e.text.append(removed); }
    }
    static final class History {
        private final Editor editor;
        private final Deque<EditCommand> done = new ArrayDeque<>();
        History(Editor editor) { this.editor = editor; }
        void run(EditCommand c) { c.run(editor); done.push(c); }
        void undo() { EditCommand c = done.poll(); if (c != null) c.undo(editor); }
    }

    public static void main(String[] args) {
        Paid order = new Placed(List.of("Dune")).pay("pay-1");
        System.out.println(order.ship("TRK-9"));
        System.out.println(order.cancel());

        Editor editor = new Editor();
        History history = new History(editor);
        history.run(new Insert("Hello"));
        history.run(new Insert(", world"));
        history.run(new DeleteLast(5));
        System.out.println(editor.text);
        history.undo(); System.out.println(editor.text);
        history.undo(); history.undo(); history.undo(); System.out.println("[" + editor.text + "]");
    }
}
