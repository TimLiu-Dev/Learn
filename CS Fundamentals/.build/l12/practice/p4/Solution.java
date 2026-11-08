import java.util.*;

public class Practice {
    static class Repo {
        final Map<String, List<String>> parents = new HashMap<>();   // commit id -> its parents (a merge commit has two)
        final Map<String, String> branches = new TreeMap<>();        // branch name -> the commit it points at
        String head = "main";                                        // the branch you're on
        private int made = 1;

        Repo() { parents.put("c1", List.of()); branches.put("main", "c1"); }

        private String newCommit(String... from) {
            String id = "c" + (++made);
            parents.put(id, List.of(from));
            return id;
        }

        String commit() { String id = newCommit(branches.get(head)); branches.put(head, id); return id; }
        void createBranch(String name) { branches.put(name, branches.get(head)); }
        void switchTo(String name) { head = name; }

        // Is `a` reachable from `b` by following parent links? (a == b counts.)
        boolean isAncestor(String a, String b) {
            if (a.equals(b)) return true;
            for (String p : parents.get(b))   // every parent: a merge commit's second parent is history too
                if (isAncestor(a, p)) return true;
            return false;
        }

        // Merges branch `other` into the current branch. Returns "up to date", "fast-forward" or "merge commit".
        String merge(String other) {
            String ours = branches.get(head);
            String theirs = branches.get(other);
            if (isAncestor(theirs, ours)) return "up to date";   // also covers ours == theirs
            if (isAncestor(ours, theirs)) {
                branches.put(head, theirs);                       // just slide the pointer forward
                return "fast-forward";
            }
            branches.put(head, newCommit(ours, theirs));
            return "merge commit";
        }
    }

    // CHECKS
    static Repo diverged() {
        Repo r = new Repo();
        r.createBranch("fix"); r.switchTo("fix"); r.commit();   // c2 on fix
        r.switchTo("main"); r.commit();                         // c3 on main
        return r;
    }

    public static void main(String[] args) {
        check("merging a branch that is ahead fast-forwards", "fast-forward c2 2", () -> {
            Repo r = new Repo(); r.createBranch("fix"); r.switchTo("fix"); r.commit(); r.switchTo("main");
            String what = r.merge("fix");
            return what + " " + r.branches.get("main") + " " + r.parents.size();
        });
        check("diverged branches get a merge commit with two parents", "merge commit c4 [c3, c2] c2", () -> {
            Repo r = diverged();
            String what = r.merge("fix");
            return what + " " + r.branches.get("main") + " " + r.parents.get("c4") + " " + r.branches.get("fix");
        });
        check("merging what you already have changes nothing", "up to date c4 4", () -> {
            Repo r = diverged(); r.merge("fix");
            String what = r.merge("fix");
            return what + " " + r.branches.get("main") + " " + r.parents.size();
        });
        check("after a merge, merging back the other way is a fast-forward", "fast-forward c4", () -> {
            Repo r = diverged(); r.merge("fix"); r.switchTo("fix");
            String what = r.merge("main");
            return what + " " + r.branches.get("fix");
        });
        check("isAncestor follows every parent", "true true false false", () -> {
            Repo r = diverged(); r.merge("fix");
            return r.isAncestor("c2", "c4") + " " + r.isAncestor("c1", "c1") + " " + r.isAncestor("c4", "c2") + " " + r.isAncestor("c3", "c2");
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
