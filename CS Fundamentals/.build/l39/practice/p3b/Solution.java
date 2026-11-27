import java.util.*;

public class Practice {
    sealed interface Doc permits Draft, InReview, ChangesRequested, Published {}
    record Draft(String text) implements Doc {}
    record InReview(String text, String reviewer) implements Doc {}
    record ChangesRequested(String text, String note) implements Doc {}
    record Published(String text) implements Doc {}

    // Command: each request is an object
    sealed interface Cmd permits Edit, Submit, Approve, RequestChanges {}
    record Edit(String text) implements Cmd {}
    record Submit(String reviewer) implements Cmd {}
    record Approve() implements Cmd {}
    record RequestChanges(String note) implements Cmd {}

    sealed interface Step permits Moved, Rejected {}
    record Moved(Doc doc) implements Step {}
    record Rejected(Doc doc) implements Step {}

    record Replayed(Doc doc, int rejected) {}

    // Switch on the state first: each state lists the commands it accepts.
    static Step step(Doc doc, Cmd cmd) {
        Doc next = switch (doc) {
            case Draft d -> switch (cmd) {
                case Edit e -> new Draft(e.text());
                case Submit s -> new InReview(d.text(), s.reviewer());
                default -> null;
            };
            case InReview r -> switch (cmd) {
                case Approve a -> new Published(r.text());
                case RequestChanges c -> new ChangesRequested(r.text(), c.note());
                default -> null;
            };
            case ChangesRequested c -> cmd instanceof Edit e ? new Draft(e.text()) : null;
            case Published p -> null;
        };
        return next == null ? new Rejected(doc) : new Moved(next);
    }

    static Replayed replay(Doc start, List<Cmd> cmds) {
        Doc doc = start;
        int rejected = 0;
        for (Cmd cmd : cmds) {
            if (step(doc, cmd) instanceof Moved m) doc = m.doc();
            else rejected++;
        }
        return new Replayed(doc, rejected);
    }

    // CHECKS
    public static void main(String[] args) {
        check("a draft can be edited and then submitted", List.of(new Moved(new Draft("v2")), new Moved(new InReview("v2", "Grace"))), () ->
            List.of(step(new Draft("v1"), new Edit("v2")), step(new Draft("v2"), new Submit("Grace"))));
        check("approving a document in review publishes it", new Moved(new Published("v2")), () ->
            step(new InReview("v2", "Grace"), new Approve()));
        check("requested changes go back to draft by editing",
            List.of(new Moved(new ChangesRequested("v2", "cite it")), new Moved(new Draft("v3"))), () ->
            List.of(step(new InReview("v2", "Grace"), new RequestChanges("cite it")),
                    step(new ChangesRequested("v2", "cite it"), new Edit("v3"))));
        check("illegal moves are rejected and leave the document unchanged", 0, () -> {
            Doc draft = new Draft("v1"), review = new InReview("v1", "Grace");
            Doc asked = new ChangesRequested("v1", "cite it"), published = new Published("v1");
            List<Step> got = new ArrayList<>(List.of(step(draft, new Approve()), step(review, new Edit("sneaky")),
                                                     step(asked, new Submit("Grace"))));
            for (Cmd c : List.of(new Edit("v2"), new Submit("Grace"), new Approve(), new RequestChanges("no")))
                got.add(step(published, c));
            List<Step> want = List.of(new Rejected(draft), new Rejected(review), new Rejected(asked),
                new Rejected(published), new Rejected(published), new Rejected(published), new Rejected(published));
            return got.equals(want) ? 0 : got; });
        check("replay applies commands in order and counts rejections", new Replayed(new Published("v2"), 3), () ->
            replay(new Draft("v1"), List.of(new Approve(), new Submit("Grace"), new RequestChanges("cite it"),
                new Approve(), new Edit("v2"), new Submit("Linus"), new Approve(), new Edit("v3"))));
        check("replaying nothing changes nothing", new Replayed(new Draft("v1"), 0), () ->
            replay(new Draft("v1"), List.of()));
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
