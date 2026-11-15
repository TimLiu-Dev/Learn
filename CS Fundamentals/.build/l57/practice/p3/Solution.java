import java.util.List;
import java.util.Objects;

public class Practice {
    // What a voter remembers. votedFor is null when it has not voted in currentTerm;
    // lastLogIndex and lastLogTerm are 0 for an empty log.
    record Voter(long currentTerm, Integer votedFor, int lastLogIndex, long lastLogTerm) { }
    record RequestVote(long term, int candidateId, int lastLogIndex, long lastLogTerm) { }
    record Reply(Voter voter, boolean granted) { }

    // A later last term wins outright; only on a tie does the longer log win.
    static boolean candidateIsUpToDate(Voter v, RequestVote req) {
        return req.lastLogTerm() > v.lastLogTerm()
            || (req.lastLogTerm() == v.lastLogTerm() && req.lastLogIndex() >= v.lastLogIndex());
    }

    static Reply onRequestVote(Voter v, RequestVote req) {
        if (req.term() < v.currentTerm()) return new Reply(v, false);          // a stale candidate
        Voter voter = req.term() > v.currentTerm()                             // a newer term always wins
            ? new Voter(req.term(), null, v.lastLogIndex(), v.lastLogTerm())
            : v;
        boolean voteFree = voter.votedFor() == null || voter.votedFor() == req.candidateId();
        if (voteFree && candidateIsUpToDate(voter, req)) {
            return new Reply(new Voter(voter.currentTerm(), req.candidateId(), voter.lastLogIndex(), voter.lastLogTerm()), true);
        }
        return new Reply(voter, false);
    }

    // CHECKS
    public static void main(String[] args) {
        check("a fresh voter grants and remembers its vote", new Reply(new Voter(3, 1, 5, 2), true),
            () -> onRequestVote(new Voter(3, null, 5, 2), new RequestVote(3, 1, 5, 2)));
        check("a request from an older term is refused and changes nothing", new Reply(new Voter(5, null, 0, 0), false),
            () -> onRequestVote(new Voter(5, null, 0, 0), new RequestVote(4, 1, 9, 4)));
        check("one vote per term", new Reply(new Voter(3, 2, 0, 0), false),
            () -> onRequestVote(new Voter(3, 2, 0, 0), new RequestVote(3, 1, 0, 0)));
        check("the same candidate may ask again", new Reply(new Voter(3, 2, 0, 0), true),
            () -> onRequestVote(new Voter(3, 2, 0, 0), new RequestVote(3, 2, 0, 0)));
        check("a newer term frees the vote", new Reply(new Voter(4, 1, 4, 3), true),
            () -> onRequestVote(new Voter(3, 2, 4, 3), new RequestVote(4, 1, 4, 3)));
        check("the last term decides before the length", List.of(true, false, false, true), () -> {
            Voter v = new Voter(5, null, 10, 2);
            return List.of(onRequestVote(v, new RequestVote(5, 1, 3, 3)).granted(),
                           onRequestVote(v, new RequestVote(5, 1, 20, 1)).granted(),
                           onRequestVote(v, new RequestVote(5, 1, 9, 2)).granted(),
                           onRequestVote(v, new RequestVote(5, 1, 10, 2)).granted());
        });
        check("a newer term is adopted even when the vote is refused", new Reply(new Voter(5, null, 10, 3), false),
            () -> onRequestVote(new Voter(3, 2, 10, 3), new RequestVote(5, 1, 4, 2)));
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
