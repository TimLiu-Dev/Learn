import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    record Commit(String hash, String subject) { }

    /** The todo list `git rebase -i --autosquash` starts from. Commits are oldest first. */
    static List<String> autosquash(List<Commit> commits) {
        throw new UnsupportedOperationException("TODO: start with every commit as a pick line, then move the fixup! and squash! ones");
    }

    // CHECKS
    static List<Commit> commits(String... subjects) {
        List<Commit> list = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++) list.add(new Commit("c" + i, subjects[i]));
        return list;
    }

    public static void main(String[] args) {
        check("ordinary commits are picked in order", List.of("pick c0 # Add total", "pick c1 # Add discount"),
                () -> autosquash(commits("Add total", "Add discount")));
        check("a fixup moves right after its target",
                List.of("pick c0 # Add discount", "fixup c3 # fixup! Add discount", "pick c1 # Add total", "pick c2 # Document units"),
                () -> autosquash(commits("Add discount", "Add total", "Document units", "fixup! Add discount")));
        check("squash! gets the squash action", List.of("pick c0 # Add total", "squash c2 # squash! Add total", "pick c1 # Add discount"),
                () -> autosquash(commits("Add total", "Add discount", "squash! Add total")));
        check("two fixups for one commit keep their order",
                List.of("pick c0 # Add discount", "fixup c2 # fixup! Add discount", "fixup c3 # fixup! Add discount", "pick c1 # Add total"),
                () -> autosquash(commits("Add discount", "Add total", "fixup! Add discount", "fixup! Add discount")));
        check("a fixup with no target on the branch stays a pick", List.of("pick c0 # Add total", "pick c1 # fixup! Add tax"),
                () -> autosquash(commits("Add total", "fixup! Add tax")));
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
