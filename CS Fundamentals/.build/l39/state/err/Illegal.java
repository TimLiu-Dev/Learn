import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Illegal {
    // Each state is its own record, and transitions are methods on the state they start from.
    sealed interface Loan permits Reserved, OnLoan, Returned, Cancelled {}
    record Reserved(String member) implements Loan {
        OnLoan pickUp(LocalDate today) { return new OnLoan(member, today.plusDays(14)); }
        Cancelled cancel() { return new Cancelled(); }
    }
    record OnLoan(String member, LocalDate due) implements Loan {
        Returned giveBack(LocalDate today) {
            return new Returned(member, (int) Math.max(ChronoUnit.DAYS.between(due, today), 0) * 25);
        }
    }
    record Returned(String member, int feeCents) implements Loan {}
    record Cancelled() implements Loan {}

    // Exhaustive pattern switch (Java 21+): no default. Add a fifth state and this stops compiling.
    static String describe(Loan loan) {
        return switch (loan) {
            case Reserved r -> "held for " + r.member();
            case OnLoan o -> "with " + o.member() + " until " + o.due();
            case Returned r -> "returned by " + r.member() + ", fee " + r.feeCents();
            case Cancelled c -> "cancelled";
        };
    }

    // Command: each request is an object, so it can be queued, logged and replayed.
    sealed interface LoanCommand permits PickUp, Cancel, GiveBack {}
    record PickUp(LocalDate today) implements LoanCommand {}
    record Cancel() implements LoanCommand {}
    record GiveBack(LocalDate today) implements LoanCommand {}

    // When you hold only a Loan, every (state, command) pair is decided here. null = illegal.
    static Loan apply(Loan loan, LoanCommand cmd) {
        return switch (loan) {
            case Reserved r -> switch (cmd) {
                case PickUp p -> r.pickUp(p.today());
                case Cancel c -> r.cancel();
                case GiveBack g -> null;                   // illegal: nothing to give back
            };
            case OnLoan o -> cmd instanceof GiveBack g ? o.giveBack(g.today()) : null;
            case Returned r -> null;                       // final states accept nothing
            case Cancelled c -> null;
        };
    }

    public static void main(String[] args) {
        new Returned("Ada", 0).pickUp(LocalDate.of(2026, 3, 1));   // illegal transition
        Reserved reserved = new Reserved("Ada");
        OnLoan onLoan = reserved.pickUp(LocalDate.of(2026, 3, 1));
        System.out.println(describe(onLoan));
        System.out.println(describe(onLoan.giveBack(LocalDate.of(2026, 3, 18))));

        List<LoanCommand> queue = List.of(new PickUp(LocalDate.of(2026, 3, 1)), new Cancel(),
                                          new GiveBack(LocalDate.of(2026, 3, 10)));
        Loan state = reserved;
        List<Loan> history = new ArrayList<>(List.of(state));   // undo = go back to an earlier state
        for (LoanCommand cmd : queue) {
            Loan next = apply(state, cmd);
            System.out.println(cmd + " -> " + (next == null ? "rejected" : describe(next)));
            if (next != null) { state = next; history.add(next); }
        }
        history.remove(history.size() - 1);
        System.out.println("after undo: " + describe(history.get(history.size() - 1)));
    }
}
